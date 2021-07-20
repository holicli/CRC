/** * 采集上报 * * @param root * @param $ * @param factory * @author 张昕伟 * @returns */;(function (root, $, factory) {    $(function () {        root.app = factory(root, $).initialize();    });})(this, jQuery, function (window, $) {    function nullToStr(data) {       if (data==null){           return ""       }else {           return data       }    }    return {        URL: {            CHECK: '/wechat/spec/priceCheck',            REPORT: '/wechat/spec/priceReport',            DELETE: '/wechat/spec/deleteData',            ADD: '/wechat/spec/addData',            SCJY: '/wechat/spec/addScjy',            warning: '/wechat/spec/priceWarnings'        },        initialize: function () {            window.dialog = parent.dialog;            this.$table = $('#jgsb');            this.$pageup = $('#pageup');            this.$pagedown = $('#pagedown');            this.$cigNo = []; // 规格编号数组            this.$page = 1; // 当前页            this.$totalPage = 1; // 总页数            this.$status = 0;            return this.render().events();        },        render: function () {            var thiz = this;            thiz.loaddata();            // 初始化分页            thiz.$totalPage = parseInt($('#total').html().split('/')[1]);            thiz.initPage.call(thiz);            return thiz;        },        events: function () {            var thiz = this;            // $('.suggestion').on('click', function () {            //     thiz.initSuggestion.call(thiz);            // });            $(document).on('click', '.suggestion', function(e) {                thiz.initSuggestion.call(thiz);            });            // $('.suggestion').click(function(){            //     thiz.initSuggestion.call(thiz);            // });            $('.qx').on('click', function () {                thiz.hideAll.call(thiz);            });            $('.gb').on('click', function () {                thiz.success.call(thiz);            });            $('#Info_qr').on('click', function () {                thiz.hideInfo.call(thiz);            });            $('#Info_xg').on('click', function () {                thiz.hideInfos.call(thiz);            });            $(".markets-frame").find('.qd').on('click', function () {                thiz.onSuggestion.call(thiz);            });            $(".input-frame").find('.qd').on('click', function () {                thiz.onPrice.call(thiz);            });            $("#yl_info").find('.qd').on('click', function () {                thiz.onCommit.call(thiz);            });            $("#submit").on('click', function () {                thiz.onSubmit.call(thiz);            });            $("input:radio").on('click', function () {                thiz.radioBox.apply(thiz, arguments);            });            $("input").on("blur", function () {                window.scroll(0, 0); //让页面归位            });            $("#s-select").on('change', function () {                thiz.initSelect.apply(thiz, arguments);            })            thiz.$pageup.on('click', function () {                thiz.pageUp.call(thiz);            })            thiz.$pagedown.on('click', function () {                thiz.pageDown.call(thiz);            })            return thiz;        },        // 初始化分页按钮        initPage: function () {            var thiz = this;            if (thiz.$page == 1) {	// 若为第一页则图标变灰                thiz.$pageup.css('background-color', '#C0C0C0');            } else {                thiz.$pageup.css('background-color', '#229ffd');            }            if (thiz.$page == thiz.$totalPage) { // 若为最后一页则文字为提交                thiz.$pagedown.html('提交');            } else {                thiz.$pagedown.html('下一页');            }            $('#total').html(thiz.$page + "/" + thiz.$totalPage);	// 更改悬浮窗标签        },        // 初始化底部悬浮窗选择        initSelect: function (e) {            var thiz = this, $self = $(e.currentTarget);            if (thiz.saveReport.call(thiz)) thiz.$page = $self.val();	// 如果保存成功，则更改当前页为选中的值            $('.jgsb_info').css('display', 'none');            $('.jgsb_info').eq(thiz.$page - 1).css('display', 'block');            thiz.initPage.call(thiz);        },        // 点击上一页        pageUp: function () {            var thiz = this;            if (thiz.$page != 1 && thiz.saveReports.call(thiz)) {                thiz.$page--;	// 如果不是首页，并且保存成功，则页数-1                $('.jgsb_info').css('display', 'none');                $('.jgsb_info').eq(thiz.$page - 1).css('display', 'block');                $("#s-select").val(thiz.$page);                thiz.initPage.call(thiz);            }        },        // 点击下一页        pageDown: function () {            var thiz = this;            if (thiz.$page == thiz.$totalPage) {	// 如果是最后一页，点击触发提交方法                thiz.saveReport.call(thiz);                thiz.commit.call(thiz);            } else {                if (thiz.saveReport.call(thiz)) thiz.$page++; // 如果保存成功，则页数+1                $('.jgsb_info').css('display', 'none');                $('.jgsb_info').eq(thiz.$page - 1).css('display', 'block');                $("#s-select").val(thiz.$page);                thiz.initPage.call(thiz);            }        },        // 价格弹窗点击确定的事件        onPrice: function () {            var thiz = this;            var data = $(".content").val();            var $purchasePrice = $('.jgsb_info').eq(thiz.$page - 1).find(                '.purchasePrice');            var $marketPrice = $('.jgsb_info').eq(thiz.$page - 1).find(                '.marketPrice');            var $cartonPrice = $('.jgsb_info').eq(thiz.$page - 1).find(                '.cartonPrice');            var $packPrice = $('.jgsb_info').eq(thiz.$page - 1).find(                '.packPrice');            // res表示价格校验结果(0:未输入;1:输入正确;2:输入错误)            var res = thiz.checkPrice(data, $("input[class='content']").attr("attr"));            if ($(".content").attr("attr") == 4) {                if (res != 2) {                    if (res == 1)                        $purchasePrice.html(data);                    //大户出手价如果为空则继续弹出该窗口                    if ($marketPrice.html() == '') {                        thiz.initMarketPrice().call(thiz);                        return;                    }                    // 整条零售价若为空则继续弹出该窗口                    if ($cartonPrice.html() == '') {                        thiz.initCartonPrice.call(thiz);                        return;                    }                    // 单包零售价若为空则继续弹出该窗口                    if ($packPrice.html() == '') {                        thiz.initPackPrice.call(thiz);                        return;                    }                    thiz.hideAll.call(thiz);                }            } else if ($(".content").attr("attr") == 1) {                if (res != 2) {                    $marketPrice.html(data);                    //如果整条零售价为空则继续弹出该窗口                    if ($cartonPrice.html() == '') {                        thiz.initCartonPrice.call(thiz);                        return;                    }                    // 单包零售价若为空则继续弹出该窗口                    if ($packPrice.html() == '') {                        thiz.initPackPrice.call(thiz);                        return;                    }                    thiz.hideAll.call(thiz);                }            } else if ($(".content").attr("attr") == 2) {                if (res != 2) {                    $cartonPrice.html(data);                    // 单包零售价若为空则继续弹出该窗口                    if ($packPrice.html() == '') {                        thiz.initPackPrice.call(thiz);                        return;                    }                    thiz.hideAll.call(thiz);                }            } else {                if (res != 2) {                    $packPrice.html(data);                    thiz.hideAll.call(thiz);                }            }        },        // 初始化市场动态信息窗口        initSuggestion: function () {            var thiz = this;            var selector = $('.jgsb_info').eq(thiz.$page - 1).find(                '.suggestion');            $(".markets-frame").show();            $('.contents').val($(selector).html()).focus();        },        // 市场动态信息窗口点击确认的方法        onSuggestion: function () {            var thiz = this;            var selector = $('.jgsb_info').eq(thiz.$page - 1).find(                '.suggestion');            $(selector).html(thiz.checkStr($(".contents").val()));            $(".markets-frame").hide();        },        // 检查输入字符串        checkStr: function (data) {            var reg = /[\uD83C|\uD83D|\uD83E][\uDC00-\uDFFF][\u200D|\uFE0F]|[\uD83C|\uD83D|\uD83E][\uDC00-\uDFFF]|[0-9|*|#]\uFE0F\u20E3|[0-9|#]\u20E3|[\u203C-\u3299]\uFE0F\u200D|[\u203C-\u3299]\uFE0F|[\u2122-\u2B55]|\u303D|[\A9|\AE]\u3030|\uA9|\uAE|\u3030/ig;            data = $.trim(data);            if (data) {                if (reg.test(data)) {                    data = data.replace(reg, "");                    data = data.replace(" ", "");                }            }            return data;        },        checkPrice: function (purchase, market, carton, pack) {            var thiz = this;            var rest = null;            var cigaretteNo = thiz.$cigNo[thiz.$page - 1];            /*  if (!purchaseBj || !marketBj || !cartonBj || !packBj) {                  return 0;              }*/            $.ajax({                cache: false,                async: false,                type: "post",                url: this.URL.warning,                data: {                    "cigaretteNo": cigaretteNo                },                jsonType: "json",                success: function (msg) {                    var array = [];                    var data = msg;                    var minMarket = data.minMarket;                    var maxMarket = data.maxMarket;                    if (parseInt($.trim(market)) < minMarket || parseInt($.trim(market)) > maxMarket) {                        array.push("销量");                        rest = 1;                    }                    var minPurchase = data.minPurchase;                    var maxPurchase = data.maxPurchase;                    if (parseInt($.trim(purchase)) < minPurchase || parseInt($.trim(purchase)) > maxPurchase) {                        array.push("购进量");                        rest = 1;                    }                    var minCarton = data.minCarton;                    var maxCarton = data.maxCarton;                    if (parseInt($.trim(carton)) < minCarton || parseInt($.trim(carton)) > maxCarton) {                        array.push("社会库存");                        rest = 1;                    }                    var minPack = data.minPack;                    var maxPack = data.maxPack;                    if (parseInt($.trim(pack)) < minPack || parseInt($.trim(pack)) > maxPack) {                        array.push("调剂价格");                        rest = 1;                    }                    if (rest == 1) {                        array.push("不在合理区间,请点击确定进行修改");//a=6                        var content;                        content = array.join(',');                        parent.dialog({                            type: 'alert',                            content: '<span style="font-size:0.75rem;color: red">' + content + '</span>', /*+"<div style='font-size: 0.75rem;margin-top: -1.08rem;margin-left:3.1rem '>不在合理区间,请点击确定进行修改</div>",*/                            width: '13rem',                            height: '5rem',                            ok: function () {                            }                        }).showModal();                    } else {                        rest = 2;                    }                }            });            return rest;        },        // 保存上报信息        saveReport: function () {            var thiz = this;            var flag = null;            var divObj = $('.jgsb_info').eq(thiz.$page - 1);            var reg = /(^$)|(^\d+(\.\d+)?$)/;            var purchase = divObj.find("#purchasePrice").val();            var purchasePrice = reg.test(purchase);            var market = divObj.find("#marketPrice").val();            var marketPrice = reg.test(market);            var carton = divObj.find("#cartonPrice").val();            var cartonPrice = reg.test(carton);            var pack = divObj.find("#packPrice").val();            var packPrice = reg.test(pack);            //rest=1  预警提示  rest=2 预警通过            if (purchasePrice && marketPrice && cartonPrice && packPrice) {                var rest = thiz.checkPrice(purchase, market, carton, pack);                if (rest != 1) {                    var cigaretteNo = thiz.$cigNo[thiz.$page - 1];                    var closeDate = $('#day').attr('data');                    var marketPrice = divObj.find("#marketPrice").val();                    var purchasePrice = divObj.find("#purchasePrice").val();                    var cartonPrice = divObj.find("#cartonPrice").val();                    var packPrice = divObj.find("#packPrice").val();                    var suggestion = divObj.find(".suggestion").html();                    $.ajax({                        url: thiz.URL.REPORT,                        data: {                            "cigaretteNo": cigaretteNo,                            "closeDate": closeDate,                            "marketPrice": marketPrice,                            "purchasePrice": purchasePrice,                            "cartonPrice": cartonPrice,                            "packPrice": packPrice,                            "suggestion": encodeURI(suggestion, "utf-8")                        },                        dataType: 'json',                        type: "POST",                        async: false,                        success: function (result) {                            if (result.success) {                                flag = true;                            } else {                                $('#Info').html("操作失败");                                $('#Info_xxk').css('display', 'block');                                flag = false;                            }                        }                    });                    return flag;                }            } else {                parent.dialog({                    type: 'alert',                    content: '<span style="font-size:0.75rem;color: red;margin-left: 4rem"> 只能输入数字</span>',                    width: '13rem',                    height: '5rem',                    ok: function () {                    }                }).showModal();            }        },        // 保存上报信息        saveReports: function () {            var thiz = this;            var flag = null;            var divObj = $('.jgsb_info').eq(thiz.$page - 1);            var cigaretteNo = thiz.$cigNo[thiz.$page - 1];            var closeDate = $('#day').attr('data');            var marketPrice = divObj.find("#marketPrice").val();            var purchasePrice = divObj.find("#purchasePrice").val();            var cartonPrice = divObj.find("#cartonPrice").val();            var packPrice = divObj.find("#packPrice").val();            var suggestion = divObj.find(".suggestion").html();            $.ajax({                url: thiz.URL.REPORT,                data: {                    "cigaretteNo": cigaretteNo,                    "closeDate": closeDate,                    "marketPrice": marketPrice,                    "purchasePrice": purchasePrice,                    "cartonPrice": cartonPrice,                    "packPrice": packPrice,                    "suggestion": encodeURI(suggestion, "utf-8")                },                dataType: 'json',                type: "POST",                async: false,                success: function (result) {                    if (result.success) {                        flag = true;                    } else {                        $('#Info').html("操作失败");                        $('#Info_xxk').css('display', 'block');                        flag = false;                    }                }            });            return flag;        },        // 点击提交展示预览页面        commit: function () {            var thiz = this;            var data = "";            thiz.$table.children().each(function (index, jgsb) {                if ($('.jgsb_info').eq(index).find("#marketPrice").val() == "" &&                    $('.jgsb_info').eq(index).find("#cartonPrice").val() == "" &&                    $('.jgsb_info').eq(index).find("#packPrice").val() == "")                    data += "";                else                    data += $(jgsb).attr('id') + ";";            });            if (data == "") {                $.ajax({                    type: "post",                    url: thiz.URL.DELETE,                    async: false,                    data: {"closeDate": $('#day').attr('data')},                    dataType: "json",                    success: function (result) {                        $('#Info').html("没有上报信息");                        $('#Info_xxk').css('display', 'block');                        thiz.$status = 1;                    }                });            } else {                var yl_html = '';                thiz.$table.children().each(function (index, jgsb) {                    if (!($('.jgsb_info').eq(index).find("#marketPrice").val() == "" &&                            $('.jgsb_info').eq(index).find("#cartonPrice").val() == "" &&                            $('.jgsb_info').eq(index).find("#packPrice").val() == "")) {                        var purchasePrice = $('.jgsb_info').eq(index).find("#purchasePrice").val();                        var marketPrice = $('.jgsb_info').eq(index).find("#marketPrice").val();                        var cartonPrice = $('.jgsb_info').eq(index).find("#cartonPrice").val();                        var packPrice = $('.jgsb_info').eq(index).find("#packPrice").val();                        var suggestion = $('.jgsb_info').eq(index).find(".suggestion").html();                        var name = $('.jgsb_info').eq(index).find(".pri").find("h3").html();                        yl_html += '<tr><td colspan="5" class="name">' + name + '</td></tr><tr class="tr-bg"><td>进购量</td><td>销量</td><td>社会库存</td><td>调剂价格</td></tr>' +                            '<tr><td>' + purchasePrice + '</td><td>' + marketPrice + '</td><td>' + cartonPrice + '</td><td>' + packPrice + '</td></tr><tr><td colspan="5">市场动态信息</td></tr><tr><td colspan="5" class="info">' + suggestion + '</td></tr><tr class="tr-line"><td colspan="5"></td></tr>';                    }                });                $('#select').html(yl_html);                $('#yl_info').css('display', 'block');            }        },        // 上报信息提交确认        onCommit: function () {            var thiz = this;            var data = "";            thiz.$table.children().each(function (index, jgsb) {                if (!($('.jgsb_info').eq(index).find("#marketPrice").val() == "" &&                        $('.jgsb_info').eq(index).find("#cartonPrice").val() == "" &&                        $('.jgsb_info').eq(index).find("#packPrice").val() == ""))                    data += $(jgsb).attr('id') + ";";            });            $('#yl_info').css('display', 'none');            //插入上报信息            $.ajax({                type: "post",                url: thiz.URL.ADD,                async: false,                data: {"cigNo": data, "closeDate": $('#day').attr('data')},                dataType: "json",                success: function (result) {                    if (result.success) {                        alert("提交成功")                        location.href = "/wechat/index"                    } else {                        $('#Info').html(result.message);                        $('#Info_xxk').css('display', 'block');                        thiz.$status = 1;                    }                }            });        },        // 市场总体情况提交确认        onSubmit: function () {            var thiz = this;            var data = $('#scjy_content').val();            var a= $('#day')            var b= $('#day').attr('data')            var c= $('#day').val()            if (data) {                thiz.checkStr(data);                $.ajax({                    type: "post",                    url: thiz.URL.SCJY,                    async: false,                    data: {"data": encodeURI(data, "utf-8"), "closeDate": $('#day').attr('data')},                    dataType: "json",                    success: function (result) {                        if (result.success) {                            thiz.success.call(thiz);                        } else {                            $('#Info').html(result.message);                            $('#Info_xxk').css('display', 'block');                            thiz.$status = 1;                        }                    }                });            } else {                thiz.success.call(thiz);            }        },        // 单选框的方法，点击已选中的取消选择        radioBox: function (e) {            var $self = $(e.currentTarget);            var domName = $self.attr('name');// 获取当前单选框控件name 属性值            var $other = $("input:radio[name='" + domName + "']");            var checkedState = $self.attr('data-status');// 记录当前选中状态            if (checkedState == 0) {                // 取消与当前控件name 相同的所有控件的选中状态                $other.removeAttr('checked');                $self.prop('checked', true);// 选中当前控件                $other.attr('data-status', 0);                $self.attr('data-status', 1);            } else if (checkedState == 1) {                // 如果当前控件在点击前是选中状态，则点击后取消其选中状态                $self.removeAttr('checked');                $self.attr('data-status', 0);            }        },        // 隐藏所有弹窗        hideAll: function () {            $(".markets-frame").hide();            $(".input-frame").hide();            $("#yl_info").hide();        },        // 隐藏信息弹窗        hideInfo: function () {            var thiz = this;            if (thiz.$status == 0) {                $('#Info_xxk').css('display', 'none');                $(".input-frame").show();            } else if (thiz.$status == 1) {                window.location.href = "index";            }        },        hideInfos: function () {            var thiz = this;            if (thiz.$status == 0) {                $('#Info_xxk2').css('display', 'none');                $(".input-frame").show();            } else if (thiz.$status == 1) {                window.location.href = "index";            }        },        // 提交成功提示弹窗        success: function () {            $(".frame-box").hide();            $(".success-box").show();        },        loaddata:function (){            var thiz = this;            $.ajax({                type: "post",                url: "/wechat/spec/getReportList",                dataType: "json",                success: function (msg) {                    console.log(msg)                    var date = msg.data["cigarette"]                    var time = msg.data["reportDate"].closeDate                    html="";                    alltime = new Date(time)                    var year=alltime.getFullYear()                    var month = alltime.getMonth()+1                    var day =alltime.getDate()                    time = year+"-"+month+"-"+day                    $("#day").attr("data",time)                    $("#day").html(msg.data["reportDate"].reportPeriod)                    for (var i=0;i<date.length;i++){                        var num = i+1;                        $("#s-select").append("<option value="+num+">"+date[i].cigaretteName+"</option>")                        html+="<div class=\"jgsb_info\" style=\"display: none\"\n" +                            "                     id="+date[i].cigaretteNo+">\n" +                            "                    <div class=\"price-head\">\n" +                            "                        <div class=\"price\">\n" +                            "                            <div class=\"pic\">\n" +                            "                                <img src="+date[i].url+"/>\n" +                            "                            </div>\n" +                            "                            <div class=\"pri\">\n" +                            "                                <h3>"+date[i].cigaretteName+"</h3>\n" +                            "                                <p>"+date[i].company+"</p>\n" +                            "                                <label>批&nbsp;&nbsp;&nbsp;发&nbsp;&nbsp;&nbsp;&nbsp;价：\n" +                            "                                    <span data="+date[i].wholesalePrice+">"+date[i].wholesalePrice+"&nbsp;&nbsp;&nbsp;元/条</span>\n" +                            "                                </label>\n" +                            "                                <label>零售指导价：\n" +                            "                                    <span data="+date[i].retailPrice+">"+date[i].retailPrice+"&nbsp;&nbsp;&nbsp;元/条</span>\n" +                            "                                </label>\n" +                            "                            </div>\n" +                            "                        </div>\n" +                            "                    </div>\n" +                            "                    <div class=\"price-content\">\n" +                            "                        <div class=\"markets\">\n" +                            "                            <div>\n" +                            "                                <div>购进量 ：<input type=\"number\" pattern=\"[0-9]*\" class=\"m-input\" id=\"purchasePrice\"\n" +                            "                                                 value="+date[i].purchasePrice+" placeholder="+nullToStr(date[i].purchasePrice)+">箱\n" +                            "                                </div>\n" +                            "                            </div>\n" +                            "                            <div>\n" +                            "                                <div>销 量 ：<input type=\"number\" pattern=\"[0-9]*\" class=\"m-input\" id=\"marketPrice\"\n" +                            "                                                  value="+date[i].marketPrice+" placeholder="+nullToStr(date[i].marketPrice)+">箱\n" +                            "                                </div>\n" +                            "                            </div>\n" +                            "                            <p class=\"a2\" style=\"display: none\">请检查上报价格是否准确，如果无误请忽略本提示!</p>\n" +                            "                            <div>\n" +                            "                                <div>社会库存：<input type=\"number\"  pattern=\"[0-9]*\" class=\"m-input\" id=\"cartonPrice\"\n" +                            "                                                  value="+date[i].cartonPrice+" placeholder="+nullToStr(date[i].cartonPrice)+">箱\n" +                            "                                </div>\n" +                            "\n" +                            "                            </div>\n" +                            "                            <div>\n" +                            "                                <div>调剂价格：<input type=\"number\" pattern=\"[0-9]*\" class=\"m-input\" id=\"packPrice\"\n" +                            "                                                  value="+date[i].packPrice+" placeholder="+nullToStr(date[i].packPrice)+">元/箱\n" +                            "                                </div>\n" +                            "\n" +                            "                            </div>\n" +                            "                        </div>\n" +                            "                        <div class=\"text-choice\" style=\"margin-bottom: 20%;\">\n" +                            "                            <h3>\n" +                            "                                *动态信息<span>&nbsp;&nbsp;(此项为鼓励加分项，选填)</span>\n" +                            "                            </h3>\n" +                            "                            <div class=\"ques-text\">\n" +                            "                                <p class=\"suggestion\">"+nullToStr(date[i].suggestion)+"</p>\n" +                            "                            </div>\n" +                            "                        </div>\n" +                            "                    </div>\n" +                            "                </div>"                    }                    $("#jgsb").append(html)                    $("#total").html("1/"+date.length)                    // 初始化显示第一个                    thiz.$table.children(':first').css('display', 'block');                    // 初始化规格编号                    thiz.$table.children().each(function (index, jgsb) {                        thiz.$cigNo[index] = $(jgsb).attr('id');                    });                    // 初始化分页                    thiz.$totalPage = parseInt($('#total').html().split('/')[1]);                }            });        }    }});