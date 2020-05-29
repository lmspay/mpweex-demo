<template>
    <scroller class="wrapper" show-scrollbar="false">
        <div class="panel">
            <div class="charge_tip">
                <image class="c_icon" :src="icon"/>
                <text class="c_lab">{{tiplabel}}</text>
            </div>
            <text class="amount">¥{{pageParams.totalAmount / 100}}元</text>
            <div class="sp"></div>
            <div class="cell">
                <text class="k">收款方</text>
                <text class="v">{{mpParams.companyname}}</text>
            </div>
            <div class="cell">
                <text class="k">商品名称</text>
                <text class="v">{{pageParams.orderDesc}}</text>
            </div>
            <div class="sp" v-if="result == false"></div>
            <text class="button" @click="onBtnClicked" v-if="result == false">{{btnlabel}}</text>
        </div>
    </scroller>
</template>

<script>
    var navigator = weex.requireModule('navigator');
    var stream = weex.requireModule('stream');
    var modal = weex.requireModule('modal');
    import Utils from './utils.js';

    export default {
        name: 'App',
        data () {
            return {
                icon: "mpweex://images/loadding.gif",
                tiplabel: '正在查询...',
                btnlabel: '正在查询...',
                pageParams: {},
                mpParams: {},
                result: false
            }
        },
        methods: {
            onBtnClicked () {
                this.loadData();
            },
            loadData() {
                var self = this;
                self.btnlabel = '正在查询...';

                var signParams = {};
                signParams["orderno"] = self.pageParams["orderno"];
                var retSignData = Utils.getSign("GET", '/pay/checkorder', signParams);

                stream.fetch({
                    method: 'POST',
                    url: Utils.apiUrl('/pay/checkorder'),
                    type: 'json',
                    headers: {
                        "timestamp": retSignData["timestamp"],
                        "acctoken": retSignData["acctoken"]
                    },
                    body: "orderno=" + self.pageParams["orderno"]
                }, function (ret) {
                    if (!ret.ok) {
                        modal.toast({
                            message: "查询订单失败",
                            gravity: "center",
                            duration: 1
                        });
                    } else {
                        if(ret.data["status"] !== 2) {
                            self.icon = "mpweex://images/warnning_red.png";
                            self.tiplabel = "支付失败";
                            self.result = false;
                            self.btnlabel = '重新查询支付结果';
                        }else {
                            self.icon = "mpweex://images/select_active.png";
                            self.tiplabel = "支付成功";
                            self.result = true;
                        }
                    }
                });
            }
        },
        created() {
            var self = this;
            navigator.getPageParams(function (pageParams) {
                self.pageParams = pageParams;
                navigator.getMpParams(function (mpParams) {
                    self.mpParams = mpParams;
                    self.loadData();
                });
            });
        }
    }
</script>

<style scoped>
    .wrapper {
        align-items: center;
        background-color: #F2F2F2;
    }
    .panel {
        background-color: white;
        width: 690px;
        margin: 30px;
        align-items: center;
        padding: 30px;
        border-radius: 8px;
    }
    .charge_tip {
        flex-direction: row;
        align-items: center;
        align-self: flex-start;
    }
    .c_icon {
        width: 36px;
        height: 36px;
    }
    .c_lab {
        font-size: 32px;
        color: gray;
        margin-left: 20px;
    }
    .amount {
        font-size: 48px;
        margin: 30px;

    }
    .sp {
        width: 630px;
        height: 1px;
        background-color: #e2e2e2;
    }
    .button {
        margin-top: 30px;
        color: white;
        background-color: #1AB667;
        width: 630px;
        height: 80px;
        border-radius: 40px;
        line-height: 80px;
        text-align: center;
        font-size: 32px;
    }
    .button:active {
        background-color: #17A05A;
    }
    .cell {
        width: 630px;
        flex-direction: row;
        height: 80px;
        align-items: center;
        justify-content: space-between;
    }
    .k {
        font-size: 32px;
        color: gray;
    }
    .v {
        color: gray;
        font-size: 32px;
    }
</style>