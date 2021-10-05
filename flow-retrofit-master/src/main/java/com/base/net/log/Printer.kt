package com.base.net.log

import okio.GzipSource
import android.text.TextUtils
import android.util.Log
import okhttp3.*
import okio.Buffer
import org.json.JSONObject
import org.json.JSONArray
import org.json.JSONException
import java.io.EOFException
import java.io.IOException
import java.net.URLDecoder
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*

const val TAG_REQUEST = "Network-Request"
const val TAG_RESPONSE = "Network-Response"
private val LINE_SEPARATOR = System.getProperty("line.separator")
private const val REQUEST_UP_LINE =
    "┌────── Request ────────────────────────────────────────────────────────────────────────"
private const val RESPONSE_UP_LINE =
    "┌────── Response ───────────────────────────────────────────────────────────────────────"
private const val END_LINE =
    "└───────────────────────────────────────────────────────────────────────────────────────"
private const val DEFAULT_LINE = "│ "
private const val CORNER_UP = "┌ "
private const val CORNER_BOTTOM = "└ "
private const val CENTER_LINE = "├ "
private const val BASE_TAG = "Base:"
private const val HEADERS_TAG = "Headers:"
private const val BODY_TAG = "Body:"
private const val URL_TAG = "URL: "
private const val URL_PATH = "Path: "
private const val METHOD_TAG = "Method: "
private const val STATUS_CODED_TAG = "StatusCode: "
private const val STATUS_MESSAGE_TAG = "StatusMessage: "

internal class NetworkPrinter {
    fun printRequest(request: Request) {
        val requestBody = request.body
        if (requestBody != null && isReadable(requestBody.contentType())) {
            printTextRequest(request)
        } else {
            printFileRequest(request)
        }
    }

    fun printResponse(response: Response) {
        val responseBody = response.body
        if (responseBody != null && isReadable(responseBody.contentType())) {
            printTextResponse(response)
        } else {
            printFileResponse(response)
        }
    }

    private fun printTextRequest(request: Request) {
        loggerRequest(REQUEST_UP_LINE)
        loggerRequestBase(request)
        loggerRequest(DEFAULT_LINE)
        loggerRequestHeader(request)
        loggerRequest(DEFAULT_LINE)
        loggerRequestBody(request)
        loggerRequest(END_LINE)
    }

    private fun printFileRequest(request: Request) {
        loggerRequest(REQUEST_UP_LINE)
        loggerRequestBase(request)
        loggerRequest(DEFAULT_LINE)
        loggerRequestHeader(request)
        loggerRequest(END_LINE)
    }

    private fun printTextResponse(response: Response) {
        loggerResponse(RESPONSE_UP_LINE)
        loggerResponseBase(response)
        loggerResponse(DEFAULT_LINE)
        loggerResponseHeader(response)
        loggerResponse(DEFAULT_LINE)
        loggerResponseBody(response)
        loggerResponse(END_LINE)
    }

    private fun printFileResponse(response: Response?) {}

    /**
     * 是否为可阅读的文本信息
     */
    private fun isReadable(mediaType: MediaType?): Boolean {
        return if (mediaType == null) {
            false
        } else isText(mediaType) || isPlain(mediaType)
                || isJson(mediaType) || isForm(mediaType)
                || isHtml(mediaType) || isXml(mediaType)
    }

    private fun isText(mediaType: MediaType?): Boolean {
        return if (mediaType == null) {
            false
        } else "text" == mediaType.type
    }

    private fun isPlain(mediaType: MediaType?): Boolean {
        return mediaType?.subtype?.lowercase(Locale.getDefault())?.contains("plain") ?: false
    }

    private fun isJson(mediaType: MediaType?): Boolean {
        return mediaType?.subtype?.lowercase(Locale.getDefault())?.contains("json") ?: false
    }

    private fun isXml(mediaType: MediaType?): Boolean {
        return mediaType?.subtype?.lowercase(Locale.getDefault())?.contains("xml") ?: false
    }

    private fun isHtml(mediaType: MediaType?): Boolean {
        return mediaType?.subtype?.lowercase(Locale.getDefault())?.contains("html") ?: false
    }

    private fun isForm(mediaType: MediaType?): Boolean {
        return mediaType?.subtype?.lowercase(Locale.getDefault())?.contains("x-www-form-urlencoded")
            ?: false
    }

    private fun loggerRequestBase(request: Request) {
        loggerRequest(DEFAULT_LINE + BASE_TAG)
        loggerRequest(DEFAULT_LINE + CORNER_UP + URL_TAG + request.url)
        loggerRequest(DEFAULT_LINE + CENTER_LINE + URL_PATH + request.url.encodedPath)
        loggerRequest(DEFAULT_LINE + CORNER_BOTTOM + METHOD_TAG + request.method)
    }

    private fun loggerRequestHeader(request: Request) {
        loggerRequest(DEFAULT_LINE + HEADERS_TAG)
        val headers = request.headers.toString().split(LINE_SEPARATOR).toTypedArray()
        if (headers.size > 1) { //大于1才前缀格式化符号才有效 "┌" "└"
            for (i in headers.indices) {
                if (i == 0) {
                    loggerRequest(DEFAULT_LINE + CORNER_UP + headers[i])
                } else if (i == headers.size - 1) {
                    loggerRequest(DEFAULT_LINE + CORNER_BOTTOM + headers[i])
                } else {
                    loggerRequest(DEFAULT_LINE + CENTER_LINE + headers[i])
                }
            }
        } else {
            for (head in headers) {
                loggerRequest(CENTER_LINE + head)
            }
        }
    }

    private fun loggerRequestBody(request: Request) {
        loggerRequest(DEFAULT_LINE + BODY_TAG)
        try {
            val requestBody = request.newBuilder().build().body ?: return
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            var charset = StandardCharsets.UTF_8
            val contentType = requestBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(StandardCharsets.UTF_8)
            }
            if (isPlaintext(buffer)) {
                var json = buffer.readString(charset!!)
                if (hasUrlEncoded(json)) {
                    json = URLDecoder.decode(json, convertCharset(charset))
                }
                loggerRequest(DEFAULT_LINE + jsonFormat(json))
            }
        } catch (e: IOException) {
            e.printStackTrace()
            loggerRequest(DEFAULT_LINE + BODY_TAG + "Error")
        }
    }

    private fun loggerResponseBase(response: Response) {
        loggerResponse(DEFAULT_LINE + BASE_TAG)
        loggerResponse(DEFAULT_LINE + CORNER_UP + URL_TAG + response.request.url)
        loggerResponse(DEFAULT_LINE + CENTER_LINE + STATUS_CODED_TAG + response.code)
        loggerResponse(DEFAULT_LINE + CORNER_BOTTOM + STATUS_MESSAGE_TAG + response.message)
    }

    private fun loggerResponseHeader(response: Response) {
        loggerResponse(DEFAULT_LINE + HEADERS_TAG)
        val headers = response.headers.toString().split(LINE_SEPARATOR).toTypedArray()
        if (headers.size > 1) { //大于1才前缀格式化符号才有效 "┌" "└"
            for (i in headers.indices) {
                when (i) {
                    0 -> {
                        loggerResponse(DEFAULT_LINE + CORNER_UP + headers[i])
                    }
                    headers.size - 1 -> {
                        loggerResponse(DEFAULT_LINE + CORNER_BOTTOM + headers[i])
                    }
                    else -> {
                        loggerResponse(DEFAULT_LINE + CENTER_LINE + headers[i])
                    }
                }
            }
        } else {
            for (head in headers) {
                loggerRequest(CENTER_LINE + head)
            }
        }
    }

    private fun loggerResponseBody(response: Response) {
        loggerResponse(DEFAULT_LINE + BODY_TAG)
        try {
            val responseBody = response.body ?: return
            val source = responseBody.source()
            source.request(Long.MAX_VALUE) // Buffer the entire body.
            var buffer = source.buffer
            if ("gzip".equals(response.headers["Content-Encoding"], ignoreCase = true)) {
                GzipSource(buffer.clone()).use { gzippedResponseBody ->
                    buffer = Buffer()
                    buffer.writeAll(gzippedResponseBody)
                }
            }
            var charset = StandardCharsets.UTF_8
            val contentType = responseBody.contentType()
            if (contentType != null) {
                charset = contentType.charset(StandardCharsets.UTF_8)
            }
            if (isPlaintext(buffer)) {
                var json = buffer.clone().readString(charset!!)
                if (hasUrlEncoded(json)) {
                    json = URLDecoder.decode(json, convertCharset(charset))
                }
                val bodyLines = jsonFormat(json).split(LINE_SEPARATOR).toTypedArray()
                for (line in bodyLines) {
                    loggerResponse(DEFAULT_LINE + line)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            loggerResponse(DEFAULT_LINE + BODY_TAG + "Error")
        }
    }

    private fun loggerRequest(msg: String) {
        logger(msg, true)
    }

    private fun loggerResponse(msg: String) {
        logger(msg, false)
    }

    private fun logger(msg: String, isRequest: Boolean) {
        if (TextUtils.isEmpty(msg)) return
        if (isRequest) {
            Log.i(TAG_REQUEST, msg)
        } else {
            Log.i(TAG_RESPONSE, msg)
        }
    }


    private fun isPlaintext(buffer: Buffer): Boolean {
        return try {
            val prefix = Buffer()
            val byteCount = if (buffer.size < 64) buffer.size else 64
            buffer.copyTo(prefix, 0, byteCount)
            for (i in 0..15) {
                if (prefix.exhausted()) {
                    break
                }
                val codePoint = prefix.readUtf8CodePoint()
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                    return false
                }
            }
            true
        } catch (e: EOFException) {
            false // Truncated UTF-8 sequence.
        }
    }

    private fun convertCharset(charset: Charset?): String {
        val s = charset.toString()
        val i = s.indexOf("[")
        return if (i == -1) {
            s
        } else s.substring(i + 1, s.length - 1)
    }

    /**
     * json 格式化
     *
     * @param json
     * @return
     */
    private fun jsonFormat(json: String): String {
        var json = json
        if (TextUtils.isEmpty(json)) {
            return "Empty/Null json content"
        }
        var message: String
        try {
            json = json.trim { it <= ' ' }
            message = when {
                json.startsWith("{") -> {
                    val jsonObject = JSONObject(json)
                    jsonObject.toString(4)
                }
                json.startsWith("[") -> {
                    val jsonArray = JSONArray(json)
                    jsonArray.toString(4)
                }
                else -> {
                    json
                }
            }
        } catch (e: JSONException) {
            message = json
        } catch (error: OutOfMemoryError) {
            message = "Output omitted because of Object size"
        }
        return message
    }

    /**
     * 判断 str 是否已经 URLEncoder.encode() 过
     *
     * @param str 需要判断的内容
     * @return 返回 `true` 为被 URLEncoder.encode() 过
     */
    private fun hasUrlEncoded(str: String): Boolean {
        var encode = false
        for (i in str.indices) {
            val c = str[i]
            if (c == '%' && i + 2 < str.length) {
                // 判断是否符合urlEncode规范
                val c1 = str[i + 1]
                val c2 = str[i + 2]
                if (isValidHexChar(c1) && isValidHexChar(c2)) {
                    encode = true
                }
                break
            }
        }
        return encode
    }

    /**
     * 判断 c 是否是 16 进制的字符
     *
     * @param c 需要判断的字符
     * @return 返回 `true` 为 16 进制的字符
     */
    private fun isValidHexChar(c: Char): Boolean {
        return c in '0'..'9' || c in 'a'..'f' || c in 'A'..'F'
    }
}