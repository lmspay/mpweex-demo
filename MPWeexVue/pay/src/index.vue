<template>
  <div class="wrapper">
    <image :src="logo" class="logo" />
    <text class="greeting">聚合支付测试样例程序</text>
    <text class="btn" @click="jump2Pay">支付测试</text>
    <text class="btn" @click="jump2List">订单列表</text>
    <text class="btn" @click="jump2GetPhone" v-if="weexVersionCode >= 2802">授权获取手机号</text>
    <text class="btn" @click="jump2LeaderBoards" v-if="weexVersionCode >= 2802">跳转排行榜</text>
  </div>
</template>

<script>
const sys = weex.requireModule('sys');
const modal = weex.requireModule('modal');
const stream = weex.requireModule('stream');
const navigator = weex.requireModule('navigator');
import Utils from './utils.js';

export default {
  name: 'App',
  data () {
    return {
      logo: 'mpweex://images/logo.png',
      unionid: '',
      weexVersionCode: 0
    }
  },
  created: function() {
    this.weexVersionCode = parseInt(WXEnvironment.weexVersionCode);
    this.jump2Login();
  },
  methods: {
    jump2Login() {
      sys.login((e) => {
        if(e.ok) {
          // 拿到授权后，调用小程序自己服务端获取用户资料，需要服务端封装接口
          modal.progress({
            message: '正在获取用户信息...',
          });
          var signParams = {};
          signParams["accesstoken"] = e.data.accesstoken;
          var retSignData = Utils.getSign("GET", '/users/getuserinfo', signParams);

          stream.fetch({
            method: 'GET',
            url: Utils.apiUrl('/users/getuserinfo', {
              accesstoken: e.data.accesstoken
            }),
            headers: {
              "timestamp": retSignData["timestamp"],
              "acctoken": retSignData["acctoken"]
            },
            type: 'json'
          }, (ret) => {
            modal.dismiss();
            if(ret.ok) {
              modal.toast({message: "获取授权成功"});
              this.unionid = ret.data['unionid'];
            }else {
              modal.toast({message: ret.status + ': ' + ret.statusText});
            }
          });
        }else {
          modal.toast({message: e.statusCode + ': ' + e.data});
        }
      }, 'MPAAABbdzoiyVwAVWB_Qm4' // 调试时存在，改为你自己的小程序ID
      );
    },
    jump2Pay() {
      if(this.unionid.length < 1) {
        modal.toast({message: "请先授权登录"});
        return;
      }
      navigator.push({
        page: '/pay.js',
        title: "支付测试",
        pageParams: JSON.stringify({
          unionid: this.unionid
        })
      });
    },
    jump2List() {
      if(this.unionid.length < 1) {
        modal.toast({message: "请先授权登录"});
        return;
      }
      navigator.push({
        page: '/orders.js',
        title: "订单列表",
        pageParams: JSON.stringify({
          unionid: this.unionid
        })
      });
    },
    jump2GetPhone() {
      sys.getPhoneNumber({
        'mpid': 'MPAAABbdzoiyVwAVWB_Qm4' // 调试时存在，改为你自己的小程序ID
      }, (e) => {
        if(e.ok) {
          // 拿到授权后，调用小程序自己服务端获取用户手机号，需要服务端封装接口
          modal.progress({
            message: '正在获取用户手机号码...',
          });
          var signParams = {};
          signParams["accesstoken"] = e.data.accesstoken;
          var retSignData = Utils.getSign("GET", '/users/getphoneno', signParams);

          stream.fetch({
            method: 'GET',
            url: Utils.apiUrl('/users/getphoneno', {
              accesstoken: e.data.accesstoken
            }),
            headers: {
              "timestamp": retSignData["timestamp"],
              "acctoken": retSignData["acctoken"]
            },
            type: 'json'
          }, (ret) => {
            modal.dismiss();
            if(ret.ok) {
              modal.toast({message: "获取手机号码成功：" + ret.data.phoneno});
            }else {
              modal.toast({message: ret.status + ': ' + ret.statusText});
            }
          });
          console.log(JSON.stringify(e.data));
        }else {
          modal.toast({message: e.statusCode + ': ' + e.data});
        }
      });
    },
    jump2LeaderBoards() {
      sys.showLeaderBoards({
        'mpid': 'MPAAABbdzoiyVwAVWB_Qm4' // 调试时存在，改为你自己的小程序ID
      }, e=> {

      });
    }
  }
}
</script>

<style scoped>
  .wrapper {
    justify-content: center;
    align-items: center;
  }
  .logo {
    width: 200px;
    height: 185px;
  }
  .btn {
    text-align: center;
    font-size: 32px;
    color: #333333;
    width: 650px;
    height: 70px;
    line-height: 70px;
    border-color: #CCC;
    border-width: 1px;
    border-radius: 6px;
    margin-top: 20px;
    background-color: white;
  }
  .btn:active {
    background-color: #F2F2F2;
  }
  .greeting {
    text-align: center;
    margin-top: 50px;
    font-size: 40px;
    color: #584FC0;
    margin-bottom: 50px;
  }
  .message {
    margin: 30px;
    font-size: 32px;
    color: #727272;
  }
</style>
