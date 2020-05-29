<template>
    <scroller class="wrapper" show-scrollbar="false">
        <div class="cell_root" v-for="item in orders" :key="item.orderno" @click="jump2Detail(item)">
            <div class="desc">
                <text class="prod">购买-{{item.productdesc}}</text>
                <text class="time">交易时间: {{item.completeat}}</text>
            </div>
            <text class="amount">-{{item.payamount / 100}}</text>
        </div>
    </scroller>
</template>

<script>
    var navigator = weex.requireModule('navigator');
    var stream = weex.requireModule('stream');
    var modal = weex.requireModule('modal');
    var globalEvent = weex.requireModule('globalEvent');
    import Utils from './utils.js';

    module.exports = {
        data() {
            return {
                orders: [],
                pageParams: {}
            }
        },
        methods: {
            loadData() {
                modal.progress({
                    message: '正在获取数据...',
                });

                var signParams = {};
                signParams["uno"] = this.pageParams.unionid;
                signParams["limit"] = 9999;
                signParams["offset"] = 0;
                var retSignData = Utils.getSign("GET", '/pay/orderlist', signParams);

                stream.fetch({
                    method: 'GET',
                    url: Utils.apiUrl('/pay/orderlist', {
                        uno: this.pageParams.unionid,
                        limit: 9999,
                        offset: 0
                    }),
                    headers: {
                        "timestamp": retSignData["timestamp"],
                        "acctoken": retSignData["acctoken"]
                    },
                    type: 'json'
                }, (ret) => {
                    modal.dismiss();
                    if(ret.ok) {
                        this.orders = ret.data;
                    }else {
                        modal.toast({message: ret.status + ': ' + ret.statusText});
                    }
                });
            },
            jump2Detail(item) {
                navigator.push({
                    page: '/order_detail.js',
                    title: "订单详情",
                    pageParams: JSON.stringify({
                        unionid: this.pageParams.unionid,
                        item: item
                    })
                });
            }
        },
        created() {
            var self = this;
            globalEvent.addEventListener('WXApplicationDidBecomeActiveEvent', function(e) {
				self.loadData();
			});
            navigator.getPageParams(function (pageParams) {
                self.pageParams = pageParams;
                self.loadData();
            });
        }
    }
</script>

<style scoped>
    .wrapper {
        width: 750px;
    }
    .cell_root {
        flex-direction: row;
        margin-left: 24px;
        margin-right: 24px;
        padding-top: 16px;
        padding-bottom: 16px;
        border-bottom-width: 1px;
        border-bottom-color: #CCC;
    }
    .desc {
        flex-direction: column;
        flex: 1;
    }
    .prod {
        font-size: 32px;
    }
    .time {
        font-size: 26px;
        color: #CCC;
        margin-top: 10px;
    }
    .amount {
        font-size: 36px;
        font-weight: bold;
    }
</style>