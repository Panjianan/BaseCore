package com.tsubasa.core.common.transfor

import java.util.regex.Pattern

/***
 * <br> Project BaseCore
 * <br> Package com.tsubasa.core.common.transfor
 * <br> Description 验证的相关扩展
 * <br> Version 1.0
 * <br> Author Tsubasa
 * <br> Creation 2017/11/10 11:53
 * <br> Mender Tsubasa
 * <br> Modification 2017/11/10 11:53
 * <br> Copyright Copyright © 2012 - 2017 Tsubasa.All Rights Reserved.
 */
open class RegexPattern private constructor(val regex: String) {
    companion object {
        /** 正则：手机号（简单）*/
        val REGEX_MOBILE_SIMPLE = RegexPattern("^[1]\\d{10}$")
        /**
         * 正则：手机号（精确）
         *
         * 移动：134(0-8)、135、136、137、138、139、147、150、151、152、157、158、159、178、182、183、184、187、188
         *
         * 联通：130、131、132、145、155、156、175、176、185、186
         *
         * 电信：133、153、173、177、180、181、189
         *
         * 全球星：1349
         *
         * 虚拟运营商：170
         */
        val REGEX_MOBILE_EXACT = RegexPattern("^((13[0-9])|(14[5,7])|(15[0-3,5-9])|(17[0,3,5-8])|(18[0-9])|(147))\\d{8}$")
        /**
         * 正则：电话号码
         */
        val REGEX_TEL = RegexPattern("^0\\d{2,3}[- ]?\\d{7,8}")
        /**
         * 正则：身份证号码15位
         */
        val REGEX_ID_CARD15 = RegexPattern("^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$")
        /**
         * 正则：身份证号码18位
         */
        val REGEX_ID_CARD18 = RegexPattern("^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}([0-9Xx])$")
        /**
         * 正则：邮箱
         */
        val REGEX_EMAIL = RegexPattern("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$")
        /**
         * 正则：URL
         */
        val REGEX_URL = RegexPattern("[a-zA-z]+://[^\\s]*")
        /**
         * 正则：汉字
         */
        val REGEX_ZH = RegexPattern("^[\\u4e00-\\u9fa5]+$")
        /**
         * 正则：用户名，取值范围为a-z,A-Z,0-9,"_",汉字，不能以"_"结尾,用户名必须是6-20位
         */
        val REGEX_USERNAME = RegexPattern("^[\\w\\u4e00-\\u9fa5]{6,20}(?<!_)$")
        /**
         * 正则：yyyy-MM-dd格式的日期校验，已考虑平闰年
         */
        val REGEX_DATE = RegexPattern("^(?:(?!0000)[0-9]{4}-(?:(?:0[1-9]|1[0-2])-(?:0[1-9]|1[0-9]|2[0-8])|(?:0[13-9]|1[0-2])-(?:29|30)|(?:0[13578]|1[02])-31)|(?:[0-9]{2}(?:0[48]|[2468][048]|[13579][26])|(?:0[48]|[2468][048]|[13579][26])00)-02-29)$")
        /**
         * 正则：IP地址
         */
        val REGEX_IP = RegexPattern("((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)")

        ///////////////////////////////////////////////////////////////////////////
        // 以下摘自http://tool.oschina.net/regex
        ///////////////////////////////////////////////////////////////////////////

        /**
         * 正则：双字节字符(包括汉字在内)
         */
        val REGEX_DOUBLE_BYTE_CHAR = RegexPattern("[^\\x00-\\xff]")
        /**
         * 正则：空白行
         */
        val REGEX_BLANK_LINE = RegexPattern("\\n\\s*\\r")
        /**
         * 正则：QQ号
         */
        val REGEX_TENCENT_NUM = RegexPattern("[1-9][0-9]{4,}")
        /**
         * 正则：中国邮政编码
         */
        val REGEX_ZIP_CODE = RegexPattern("[1-9]\\d{5}(?!\\d)")
        /**
         * 正则：正整数
         */
        val REGEX_POSITIVE_INTEGER = RegexPattern("^[1-9]\\d*$")
        /**
         * 正则：负整数
         */
        val REGEX_NEGATIVE_INTEGER = RegexPattern("^-[1-9]\\d*$")
        /**
         * 正则：整数
         */
        val REGEX_INTEGER = RegexPattern("^-?[1-9]\\d*$")
        /**
         * 正则：非负整数(正整数 + 0)
         */
        val REGEX_NOT_NEGATIVE_INTEGER = RegexPattern("^[1-9]\\d*|0$")
        /**
         * 正则：非正整数（负整数 + 0）
         */
        val REGEX_NOT_POSITIVE_INTEGER = RegexPattern("^-[1-9]\\d*|0$")
        /**
         * 正则：正浮点数
         */
        val REGEX_POSITIVE_FLOAT = RegexPattern("^[1-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$")
        /**
         * 正则：负浮点数
         */
        val REGEX_NEGATIVE_FLOAT = RegexPattern("^-[1-9]\\d*\\.\\d*|-0\\.\\d*[1-9]\\d*$")

        /** 如果有其他的格式用这个 */
        fun getInstance(pattern: String) = RegexPattern(pattern)
    }
}

/**
 * 判断是否匹配正则
 *
 * @param regex 正则表达式
 * @return true: 匹配    false: 不匹配
 */
fun CharSequence?.regexIsMatch(regex: RegexPattern): Boolean {
    return isNullOrEmpty().not().and(Pattern.matches(regex.regex, this))
}