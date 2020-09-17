package com.away0x.hi.library.executor

import android.os.Handler
import android.os.Looper
import androidx.annotation.IntRange
import com.away0x.hi.library.log.HiLog
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock

/**
 * Tips
 * 支持按任务的优先级去执行,
 * 支持线程池暂停.恢复(批量文件下载，上传) ，
 * 异步结果主动回调主线程
 * todo 线程池能力监控,耗时任务检测,定时,延迟,
 */
object HiExecutor {

    private val TAG: String = "HiExecutor"
    private var isPaused: Boolean = false
    private var hiExecutor: ThreadPoolExecutor
    private var lock: ReentrantLock = ReentrantLock()
    private var pauseCondition: Condition
    private val mainHandler = Handler(Looper.getMainLooper());

    init {
        pauseCondition = lock.newCondition()

        // 当前项目可用的 cu 核数
        val cpuCount = Runtime.getRuntime().availableProcessors()
        val corePoolSize = cpuCount + 1
        val maxPoolSize = cpuCount * 2 + 1
        val blockingQueue: PriorityBlockingQueue<out Runnable> = PriorityBlockingQueue()
        val keepAliveTime = 30L
        val unit = TimeUnit.SECONDS

        val seq = AtomicLong()
        val threadFactory = ThreadFactory {
            val thread = Thread(it)
            thread.name = "hi-executor-" + seq.getAndIncrement() // hi-executor-0
            return@ThreadFactory thread
        }

        hiExecutor = object : ThreadPoolExecutor(
            corePoolSize,
            maxPoolSize,
            keepAliveTime,
            unit,
            blockingQueue as BlockingQueue<Runnable>,
            threadFactory
        ) {
            override fun beforeExecute(t: Thread?, r: Runnable?) {
                // 实现线程池的暂停/恢复
                if (isPaused) {
                    lock.lock()
                    try {
                        pauseCondition.await()
                    } finally {
                        lock.unlock()
                    }
                }
            }

            override fun afterExecute(r: Runnable?, t: Throwable?) {
                // 监控线程池耗时任务,线程创建数量,正在运行的数量
                HiLog.e(TAG, "已执行完的任务的优先级是：" + (r as PriorityRunnable).priority)
            }
        }
    }

    /** 不关心执行结果的执行 */
    @JvmOverloads
    fun execute(@IntRange(from = 0, to = 10) priority: Int = 0, runnable: Runnable) {
        hiExecutor.execute(PriorityRunnable(priority, runnable))
    }

    /** 关心执行结果的执行 */
    @JvmOverloads
    fun execute(@IntRange(from = 0, to = 10) priority: Int = 0, runnable: Callable<*>) {
        hiExecutor.execute(PriorityRunnable(priority, runnable))
    }

    /** 用于实现异步任务结果主动切换到主线程 */
    abstract class Callable<T> : Runnable {
        override fun run() {
            mainHandler.post { onPrepare() }

            val t: T = onBackground();

            // 移除所有消息.防止需要执行 onCompleted了，onPrepare 还没被执行，那就不需要执行了
            mainHandler.removeCallbacksAndMessages(null)
            mainHandler.post { onCompleted(t) }
        }

        open fun onPrepare() {
            // 任务开始前的准备，比如 loading
        }
        /** 子线程真正的去执行 */
        abstract fun onBackground(): T
        // 执行完抛到主线程
        abstract fun onCompleted(t: T)
    }

    /** 实现线程池中任务按优先级执行 */
    class PriorityRunnable(val priority: Int, private val runnable: Runnable) : Runnable, Comparable<PriorityRunnable> {
        override fun compareTo(other: PriorityRunnable): Int {
            // 比较各任务的优先级
            return if (this.priority < other.priority) 1 else if (this.priority > other.priority) -1 else 0
        }

        override fun run() {
            runnable.run() // 让真正的 runnable 任务去执行
        }
    }

    /** 暂停线程池 */
    @Synchronized
    fun pause() {
        isPaused = true
        HiLog.e(TAG, "hiexecutor is paused")
    }

    /** 恢复线程池 */
    // @Synchronized, no need
    fun resume() {
        isPaused = false
        lock.lock()
        try {
            pauseCondition.signalAll()
        } finally {
            lock.unlock()
        }
        // HiLog.e(TAG, "hiexecutor is resumed")
    }
}