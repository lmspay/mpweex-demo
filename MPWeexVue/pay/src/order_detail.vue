<template>
    <scroller class="wrapper" show-scrollbar="false">
        <div class="proc_root">
            <div class="cell_root">
                <text class="cell_lab">订单号：</text>
                <text class="cell_value">{{item.orderno}}</text>
            </div>
            <div class="cell_root cell_sp">
                <text class="cell_lab">第三方订单号：</text>
                <text class="cell_value">{{item.thirdorderno}}</text>
            </div>
            <div class="cell_root cell_sp">
                <text class="cell_lab">商品：</text>
                <text class="cell_value">{{item.productdesc}}</text>
            </div>
            <div class="cell_root cell_sp">
                <text class="cell_lab">价格：</text>
                <text class="cell_value">{{item.payamount / 100}}元</text>
            </div>
            <div class="cell_root cell_sp">
                <text class="cell_lab">支付渠道：</text>
                <text class="cell_value">{{getChannelName()}}</text>
            </div>
            <div class="cell_root cell_sp">
                <text class="cell_lab">状态：</text>
                <text class="cell_value">{{getStatusDesc(item)}}</text>
            </div>
            <div class="cell_root cell_sp">
                <text class="cell_lab">付款时间：</text>
                <text class="cell_value">{{item.payat}}</text>
            </div>
            <div class="cell_root cell_sp">
                <text class="cell_lab">完成时间：</text>
                <text class="cell_value">{{item.completeat}}</text>
            </div>
            <div class="cell_root cell_sp" v-if="item.status == 5">
                <text class="cell_lab">退款订单号：</text>
                <text class="cell_value">{{item.refundno}}</text>
            </div>
            <div class="cell_root cell_sp" v-if="item.status == 5">
                <text class="cell_lab">退款金额：</text>
                <text class="cell_value">{{item.refundamount / 100}}元</text>
            </div>
            <div class="cell_root cell_sp" v-if="item.status == 5">
                <text class="cell_lab">退款说明：</text>
                <text class="cell_value">{{item.description}}</text>
            </div>
            <div class="cell_root cell_sp" v-if="item.status == 5">
                <text class="cell_lab">退款完成时间：</text>
                <text class="cell_value">{{item.refundcompleteat}}</text>
            </div>
        </div>
        <text class="btn" @click="jump2Refund" v-if="btn">申请退款</text>
    </scroller>
</template>

<script>
    const modal = weex.requireModule('modal');
    const stream = weex.requireModule('stream');
    const navigator = weex.requireModule('navigator');
    import Utils from './utils.js';
    module.exports = {
        data() {
            return {
                unionid: '',
                item: {
                    "id": 0,
                    "orderno": "string",
                    "thirdorderno": "string",
                    "uno": "string",
                    "payamount": 0,
                    "productdesc": "string",
                    "productdetail": "string",
                    "status": 0,
                    "extra": "string",
                    "completeat": "string",
                    "payat": "string",
                    "creatat": "string",
                    "refundno": "string",
                    "thirdrefundno": "string",
                    "refundamount": 0,
                    "description": "string",
                    "refundrequestdate": "string",
                    "refundsuccessdate": "string",
                    "refundcompleteat": "string"
                },
                btn: true
            };
        },
        created: function() {
            navigator.getPageParams((pageParams) => {
                this.unionid = pageParams['unionid'];
                this.item = pageParams["item"];
                modal.progress({
                    message: '正在加载...',
                });
                this.getOrderStatus();
            });
        },
        methods: {
            getStatusDesc(item) {
                if(item.status == 1) {
                    return '待付款';
                }else if(item.status == 2) {
                    return '付款成功';
                }else if(item.status == 3) {
                    return '已关闭';
                }else if(item.status == 4) {
                    return '退款中';
                }else if(item.status == 5) {
                    return '退款成功';
                }else if(item.status == 6) {
                    return '退款失败';
                }
            },
            getOrderStatus() {
                var self = this;

                var signParams = {};
                signParams["orderno"] = self.item["orderno"];
                var retSignData = Utils.getSign("GET", '/pay/checkorder', signParams);

                stream.fetch({
                    method: 'POST',
                    url: Utils.apiUrl('/pay/checkorder'),
                    type: 'json',
                    headers: {
                        "timestamp": retSignData["timestamp"],
                        "acctoken": retSignData["acctoken"]
                    },
                    body: "orderno=" + self.item["orderno"]
                }, function (ret) {
                    modal.dismiss();
                    if (!ret.ok) {
                        modal.toast({
                            message: "查询订单失败",
                            gravity: "center",
                            duration: 1
                        });
                    } else {
                        self.item.status = ret.data["status"];
                    }
                    self.btn = (self.item.status == 2);
                });
            },
            getChannelName() {
                let detailObj = JSON.parse(this.item.productdetail);
                if('wx.appPreOrder' == detailObj['msgType']) {
                    return '微信支付';
                }else if('trade.precreate' == detailObj['msgType']) {
                    return '支付宝支付';
                }else if('uac.appOrder' == detailObj['msgType']) {
                    return '云闪付';
                }else if('applepay.order' == detailObj['msgType']) {
                    return 'Apple Pay';
                }else {
                    return '-/-';
                }
            },
            jump2Refund() {
                if(this.unionid.length < 1) {
                    modal.toast({message: "请先授权登录"});
                    return;
                }
                modal.progress({
                    message: '正在加载...',
                });

                var signParams = {};
                signParams["orderno"] = this.item.orderno;
                signParams["thirdorderno"] = this.item.thirdorderno;
                signParams["refundamount"] = this.item.payamount;
                signParams["description"] = 'test refund';
                var retSignData = Utils.getSign("GET", '/pay/refund', signParams);

                stream.fetch({
                    method: 'POST',
                    url: Utils.apiUrl('/pay/refund'),
                    type: 'json',
                    headers: {
                        "timestamp": retSignData["timestamp"],
                        "acctoken": retSignData["acctoken"]
                    },
                    body: Utils.formDataEncode({
                        orderno: this.item.orderno,
                        thirdorderno: this.item.thirdorderno,
                        refundamount: this.item.payamount,
                        description: 'test refund'
                    })
                }, (ret) => {
                    if(ret.ok) {
                        modal.toast({message: '申请退款成功'});
                        this.btn = false;
                        this.item.status = 4;
                        this.getOrderStatus();
                    }else {
                        modal.dismiss();
                        if(ret.data) {
                            modal.toast({message: ret.data.code + ': ' + ret.data.message});
                        }else {
                            modal.toast({message: ret.status + ': ' + ret.statusText});
                        }
                    }
                });
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
        border-top-width: 1px;
        border-top-color: #CCC;
    }
    .cell_lab {
        font-size: 32px;
        color: #333;
        width: 250px;
    }
    .cell_value {
        font-size: 32px;
        color: #333;
        width: 350px;
        text-align: right;
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
</style>