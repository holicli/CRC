/**
 * 登录
 * @param root
 * @param $
 * @param factory
 * @author 孙修瑞
 * @returns
 */
(function(root, $, factory){
    $(function(){
        factory(root, $).initialize();
    });
})(this, jQuery, function(window, $){
    return {
        /**
         * 远程服务器连接地址
         */
        URL: {
            LOGIN: 'login',
            INDEX: 'index'
        },

        /**
         * 程序初始化
         */
        initialize: function() {
            var thiz = this;
            thiz.$username = $('#ipt-username');
            thiz.$password = $('#ipt-password');

            thiz.$login = $('#btn-login');

            thiz.setup().events();
        },

        /**
         * Cookie中保存用户名的key值
         */
        USERNAME: 'JSUSERNAME',

        /**
         * 基础配置或插件设置
         */
        setup: function() {
            // 判断是否为顶层窗口
            if (window.top !== window.self) { window.top.location.href = this.URL.LOGIN; }

            this.$username.val() === '' ? this.$username.focus() : this.$password.focus();

            return this;
        },

        /**
         * 事件绑定
         */
        events: function() {
            var thiz = this;

            // 登录操作
            thiz.$login.on('click', function() { thiz.onLogin.apply(thiz, arguments); });
            // 回车登录
            $(document).on('keyup', function() { thiz.onEnter.apply(thiz, arguments); });
        },

        /**
         * 创建Cookie信息
         * @param   name    cookie名称
         * @param   value   cookie值
         * @param   days    有效天数
         */
        createCookie: function(name, value, days) {
            var expires = '';
            if(days) {
                var date = new Date();
                date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
                expires = '; expires=' + date.toGMTString();
            }
            document.cookie = name + '=' + encodeURI(value) + expires + '; path=/';
        },

        /**
         * 登录
         */
        onLogin: function(e) {
            var thiz = this;

            if (thiz.$username.val() === '') {
                thiz.$username.focus().parent('.input-group').addClass('has-error');
                return false;
            }
            if (thiz.$password.val() === '') {
                thiz.$password.focus().parent('.input-group').addClass('has-error');
                return false;
            }

            var $self = $(e.currentTarget).attr('disabled', true);
            setTimeout(function() {$self.removeAttr('disabled'); }, 15000);

            thiz.createCookie(thiz.USERNAME, thiz.$username.val(), 30);
            $.post(thiz.URL.LOGIN, {
                username: thiz.$username.val(),
                password: thiz.$password.val(),
                rememberMe: $('#chk-rememberMe').is(':checked')
            }, function(jsonResult) {
                setTimeout(function() {$self.removeAttr('disabled'); }, 1000);
                if (jsonResult.success) {
                    location.href = thiz.URL.INDEX;
                }
                else {
                    thiz.$password.focus();
                    alert(jsonResult.message);
                }
            }, 'json');
        },

        /**
         * 回车登录
         */
        onEnter: function(e) {
            (e && e.keyCode === 13) && this.$login.trigger('click');
        }
    };
});
