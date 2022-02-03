package org.sheedon.rr.dispatcher

import java.lang.StringBuilder
import java.util.logging.Level
import java.util.logging.Logger


/**
 * Default Logger
 * @Author: sheedon
 * @Email: sheedonsun@163.com
 * @Date: 2022/2/3 9:45 上午
 */
class DefaultLogger private constructor() {

    private val logger: Logger = Logger.getLogger(defaultTag)

    companion object {
        private var defaultTag = "RR-Dispatcher"
        internal var isShowLog = false
        internal var isShowStackTrace = false

        internal val DEBUG = Level.parse("DEBUG")

        val log = DefaultLogger()
    }


    fun showLog(showLog: Boolean) {
        isShowLog = showLog
    }

    fun showStackTrace(showStackTrace: Boolean) {
        isShowStackTrace = showStackTrace
    }

    fun debug(tag: String?, message: String?) {
        if (isShowLog) {
            val stackTraceElement = Thread.currentThread().stackTrace[3]
            logger.log(
                DEBUG, if (tag.isNullOrEmpty()) getDefaultTag() else tag,
                message + getExtInfo(stackTraceElement)
            )
        }
    }

    fun info(tag: String?, message: String?) {
        if (isShowLog) {
            val stackTraceElement = Thread.currentThread().stackTrace[3]
            logger.log(
                Level.INFO,
                if (tag.isNullOrEmpty()) getDefaultTag() else tag,
                message + getExtInfo(stackTraceElement)
            )
        }
    }

    fun warning(tag: String?, message: String?) {
        if (isShowLog) {
            val stackTraceElement = Thread.currentThread().stackTrace[3]
            logger.log(
                Level.WARNING,
                if (tag.isNullOrEmpty()) getDefaultTag() else tag,
                message + getExtInfo(stackTraceElement)
            )
        }
    }

    private fun getDefaultTag(): String {
        return defaultTag
    }

    fun getExtInfo(stackTraceElement: StackTraceElement): String {
        if (isShowStackTrace) {
            val separator = " & "
            val sb = StringBuilder("[")
            val threadName = Thread.currentThread().name
            val fileName = stackTraceElement.fileName
            val className = stackTraceElement.className
            val methodName = stackTraceElement.methodName
            val threadID = Thread.currentThread().id
            val lineNumber = stackTraceElement.lineNumber
            sb.append("ThreadId=").append(threadID).append(separator)
            sb.append("ThreadName=").append(threadName).append(separator)
            sb.append("FileName=").append(fileName).append(separator)
            sb.append("ClassName=").append(className).append(separator)
            sb.append("MethodName=").append(methodName).append(separator)
            sb.append("LineNumber=").append(lineNumber)
            sb.append(" ] ")
            return sb.toString()
        }
        return ""
    }
}

internal val log = DefaultLogger.log