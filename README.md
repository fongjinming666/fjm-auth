#尝试进行oauth2.0多种授权模式的尝试

#客户端模式
http://127.0.0.1:5050/oauth/token?grant_type=client_credentials&scope=user_read&client_id=fjm-admin&client_secret=123456

#密码模式
http://127.0.0.1:5050/oauth/token?grant_type=password&scope=user_read&client_id=fjm-admin&client_secret=123456&username=8613420016280&password=admin123456

#授权模式
http://127.0.0.1:5050/oauth/authorize?response_type=code&redirect_uri=http://127.0.0.1:5050/oauth/authorize_token&client_id=fjm-admin
填手机号 验证码
13420016280 123456

#自定义模式-短信模式
http://127.0.0.1:5050/code/sms?sms=13420016280
##执行验证
http://127.0.0.1:5050/oauth/token?grant_type=sms&scope=user_read&client_id=fjm-admin&client_secret=123456&smsCode=86&sms=13420016280&code=963650
