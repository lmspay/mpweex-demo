<template>
    <div class="wrapper">
        <div class="proc_root">
            <div class="cell_root cell_sp">
                <text class="cell_lab">商品：</text>
                <text class="cell_value">{{orderDesc}}</text>
            </div>
            <div class="cell_root cell_sp">
                <text class="cell_lab">价格：</text>
                <input class="amount_i" v-model="totalAmount"></input>
                <text class="cell_value">¥{{totalAmount / 100}}元</text>
            </div>
            <text class="cell_lab" style="margin-top: 10px;">扩展信息：</text>
            <textarea @change="textChange" class="cell_textarea" rows="6"></textarea>
        </div>
        <text class="btn" @click="jump2Pay">去支付</text>
    </div>
</template>

<script>
    const modal = weex.requireModule('modal');
    const stream = weex.requireModule('stream');
    const navigator = weex.requireModule('navigator');
    const sys = weex.requireModule('sys');
    import Utils from './utils.js';

    module.exports = {
        data() {
            return {
                unionid: '',
                orderno: '',
                appPayRequest: {},
                totalAmount: 1,
                orderDesc: '',
                payChannel: undefined,
                channelName: 'sysPayChannel',
                textValue: ''
            };
        },
        created: function() {
            this.orderDesc = 'prod-' + (new Date().getTime());
            this.payChannel = new BroadcastChannel(this.channelName);
            this.payChannel.onmessage = this.payevent;
            navigator.getPageParams((pageParams) => {
                this.unionid = pageParams['unionid'] || "";
            });
        },
        methods: {
            textChange(e) {
                this.textValue = e.value;
            },
            jump2Pay() {
                if(this.unionid.length < 1) {
                    modal.toast({message: "请先授权登录"});
                    return;
                }
                modal.progress({ message: '正在加载...'});
                sys.pay({
                    'orderDesc': this.orderDesc,
                    'totalAmount': this.totalAmount,
                    'channelName': this.channelName
                }, (ret) => {
                    if(!ret.ok) {
                        modal.toast({message: "参数非法, orderDesc, totalAmount, channelName必填"});
                    }
                    modal.dismiss();
                });
            },
            createOrder(channel, paytoken) {
                let productdetailObj = {
                    'msgType': channel,
                    'paytoken': paytoken
                };
                try {
                    let textObj = eval("(" + this.textValue + ")");
                    if(textObj) {
                        for(let k in textObj) {
                            productdetailObj[k] = textObj[k];
                        }
                    }
                } catch (error) {
                    console.log(error);
                }

                var signParams = {};
                signParams["uno"] = this.unionid;
                signParams["appid"] = WXEnvironment.appId;
                signParams["payamount"] = this.totalAmount;
                signParams["productdesc"] = this.orderDesc;
                signParams["productdetail"] = JSON.stringify(productdetailObj);
                var retSignData = Utils.getSign("GET", '/pay/order', signParams);

                stream.fetch({
                    method: 'POST',
                    url: Utils.apiUrl('/pay/order'),
                    type: 'json',
                    headers: {
                        "timestamp": retSignData["timestamp"],
                        "acctoken": retSignData["acctoken"]
                    },
                    body: Utils.formDataEncode({
                        uno: this.unionid,
                        appid: WXEnvironment.appId,
                        payamount: this.totalAmount,
                        productdesc: this.orderDesc,
                        productdetail: JSON.stringify(productdetailObj)
                    })
                }, (ret) => {
                    if(ret.ok) {
                        this.payurl = ret.data["payurl"];
                        this.orderno = ret.data["orderno"];
                        this.payChannel.postMessage({
                            'cmd': 'orderCreated',
                            'result': 'success',
                            'channel': channel,
                            'appPayRequest': JSON.parse(this.payurl)
                        });
                    }else {
                        this.payChannel.postMessage({
                            'cmd': 'orderCreated',
                            'result': 'fail',
                            'message': "下单失败"
                        });
                        modal.toast({message: ret.status + ': ' + ret.statusText});
                    }
                });
            },
            onPayChannelSelected(channel, paytoken) {
                console.log('onPayChannelSelected');
                // 支付渠道选择完成，去下单
                if('wx.appPreOrder' == channel) {
                    // 微信订单
                    this.createOrder(channel, paytoken);
                }else if('trade.precreate' == channel) {
                    // 支付宝订单
                    this.createOrder(channel, paytoken);
                }else if('uac.appOrder' == channel) {
                    // 云闪付或AndroidPay订单
                    this.createOrder(channel, paytoken);
                }else if('applepay.order' == channel) {
                    // ApplePay
                    this.payChannel.postMessage({
                        'cmd': 'orderCreated',
                        'result': 'fail',
                        'message': "暂不支持ApplePay"
                    });
                    modal.toast({message: "暂不支持ApplePay"});
                }else {
                    this.payChannel.postMessage({
                        'cmd': 'orderCreated',
                        'result': 'fail',
                        'message': "非法支付渠道"
                    });
                    modal.toast({message: "非法支付渠道"});
                }
            },
            payevent(e) {
                console.log("origin: " + JSON.stringify(e));

                let data = e.data;
                if('payChannelSelected' == data['cmd']) {
                    // 支付渠道选择完成，去下单
                    this.onPayChannelSelected(data['channel'], data['paytoken']);
                }else if('onError' == data['cmd']) {
                    // 处理错误，可结束本次交易
                    if('cancel' == data['errorCode']) {
                    // 处理交易取消
                    modal.toast({message: '用户主动取消交易'});
                    }else {
                    modal.toast({message: data['errorMessage']});
                    }
                }else if('payEnd' == data['cmd']) {
                    if('success' == data['errorCode']) {
                        // 已完成付款，查询一次，如果结果为失败也需要跳转结果页，
                        // 让用户可以主动查询结果，结果确认主动权交于用户
                    }else {
                        // 付款遇到问题，至少查询一次支付结果，决定跳转逻辑
                        
                    }
                    // 支付示例统一跳转支付结果页，进行查询，实际项目根据业务需求处理
                    navigator.push({
                        title: '支付结果',
                        page: '/pay_result.js',
                        pageParams: {
                            totalAmount: this.totalAmount,
                            orderDesc: this.orderDesc,
                            orderno: this.orderno
                        },
                        finish: true
                    });
                }
            }
        }
    }
</script>

<style scoped>
    .wrapper {
        justify-content: flex-start;
        align-items: center;
    }
    .proc_root {
        border-width: 1px;
        border-color: #CCC;
        border-radius: 16px;
        padding: 32px;
        margin-top: 60px;
    }
    .cell_root{
        justify-content: flex-start;
        align-items: center;
        flex-direction: row;
        width: 600px;
        padding-top: 16px;
        padding-bottom: 16px;
    }
    .cell_sp {
        border-bottom-width: 1px;
        border-bottom-color: #CCC;
    }
    .cell_lab {
        font-size: 32px;
        color: #333;
    }
    .cell_value {
        font-size: 32px;
        color: #333;
    }
    .cell_textarea {
        width: 600px;
        height: 280px;
        padding: 20px;
        border-width: 1px;
        border-style: solid;
        border-color: #CCC;
        margin-top: 20px;
        line-height: 40px;
    }
    .btn {
        text-align: center;
        font-size: 32px;
        color: white;
        width: 664px;
        height: 70px;
        line-height: 70px;
        border-radius: 6px;
        margin-top: 20px;
        background-color: #584FC0;
    }
    .btn:active {
        background-color: #463DA7;
    }
    .amount_i {
        width: 100px;
        height: 50px;
        padding: 8px;
        margin-right: 12px;
        border-width: 1px;
        border-style: solid;
        border-color: #CCC;
    }
</style>