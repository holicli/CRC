/**
 * 微信端首页
 *
 * @param root
 * @param $
 * @param factory
 * @author 张昕伟
 * @returns
 */
;(function (root, $, factory) {
    $(function () {
        factory(root, $).initialize();
    });
})(this, jQuery, function (window, $) {
    return {
        URL: {
            MSGCOUNT: 'wechat/message/unread',
            NOTICE: 'wechat/message/notice'
        },

        initialize: function () {
            this.$num = 10;
            return this.render().events();
        },

        render: function () {
            var thiz = this;
            $.post(thiz.URL.MSGCOUNT, function (jsonResult) {
                if (jsonResult.data > 0)
                    $('#xxInfo_count,#Info_xxk').show();
            }, 'json');
            thiz.initNotice();
            return thiz;
        },
		events : function() {
			var thiz=this;
            $('#ck,.bk').on('click', function() {
                var url =$('#ck').attr('href');
                if(url==""){
                    $('#Info_market').show();
                }
                return false;
            });

            $('#Info_qr,#Info_search').on('click', function () {
                $('#Info_xxk,#Info_market').hide();
            });
            $('#notice_qd').on('click', function () {
                $('#notice').hide();
            });
            return thiz;
        },
        initNotice: function () {
            var thiz = this;
            if (thiz.readCookie('notice') != 1) {
                $.post(thiz.URL.NOTICE, function (result) {
                    if (result[0].content != "" && result[0].content != null) {
                        var html = [];
                        html.push("&#12288;&#12288;");
                        html.push(result[0].content);
                        $('#notice_info').html(html.join(""));
                        $('#notice').show();
                        setTimeout(function () {
                            $('#notice_qd').css('display', 'block');
                            $('#notice_time').css('display', 'none');
                        }, 10000); //10秒后出现确认按钮
                        thiz.setCookie("notice",1);
                        thiz.clock();
                    }
                }, 'json');
            }
        },
        clock: function () {
            var thiz = this;
            $("#time").html(thiz.$num);
            if (thiz.$num > 0) {
                thiz.$num = thiz.$num - 1;
                setTimeout(function () {
                    thiz.clock();
                }, 1000);
            }
        },
        /**
         * 读取Cookie内容
         * @param   name cookie名称
         */
        readCookie: function(name) {
            var nameEQ = name + '=';
            var ca = document.cookie.split(';');
            for(var i = 0,len = ca.length; i < len; i++) {
                var c = ca[i];
                while (c.charAt(0) == ' ') {
                    c = c.substring(1, c.length);
                }
                if (c.indexOf(nameEQ) == 0) {
                    return decodeURI(c.substring(nameEQ.length, c.length));
                }
            }
            return null;
        },
        /**
         * 添加Cookie
         * @param {string} name cookie名称
         * @param {string} value cookie值
         */
        setCookie: function(name,value) {
            var hours = 2,exp = (new Date);
            exp.setTime(exp.getTime() + hours*60*60*1000);
            document.cookie = name + '='+ escape(value) + ';expires=' + exp.toGMTString();
        }
    }
});