package update

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import com.liulishuo.filedownloader.*
import extension.*
import util.FileDownloadUtil
import util.NetUtil
import util.SPUtil
import util.SignMd5Util
import java.io.File


/**
 * Created by Teprinciple on 2016/12/13.
 */
internal object DownloadAppUtils {

    const val KEY_OF_SP_APK_PATH = "KEY_OF_SP_APK_PATH"

    /**
     * apk 下载后本地文件路径
     */
    var downloadUpdateApkFilePath: String = ""

    /**
     * 更新信息
     */
    private val updateInfo by lazy { UpdateAppUtils.updateInfo }

    /**
     * context
     */
    private val context by lazy { globalContext()!! }

    /**
     * 是否在下载中
     */
    var isDownloading = false

    /**
     *下载进度回调
     */
    var onProgress: (Int) -> Unit = {}

    /**
     * 下载出错回调
     */
    var onError: () -> Unit = {}

    /**
     * 出错，点击重试回调
     */
    var onReDownload: () -> Unit = {}

    /**
     * 通过浏览器下载APK包
     */
    fun downloadForWebView(url: String) {
        val uri = Uri.parse(url)
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    /**
     * 出错后，点击重试
     */
    fun reDownload() {
        onReDownload.invoke()
        download()
    }

    /**
     * App下载APK包，下载完成后安装
     */
    fun download() {
        (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED).no {
            log("没有SD卡")
            onError.invoke()
            return
        }

        var filePath = ""
        (updateInfo.config.apkSavePath.isNotEmpty()).yes {
            filePath = updateInfo.config.apkSavePath
        }.no {
            // 适配Android10
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Environment.isExternalStorageLegacy()) {
                filePath = (context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath
                        ?: "") + "/apk"
            } else {
                val packageName = context.packageName
                filePath = Environment.getExternalStorageDirectory().absolutePath + "/" + packageName
            }
        }

        // apk 保存名称
        val apkName = if (updateInfo.config.apkSaveName.isNotEmpty()) {
            updateInfo.config.apkSaveName
        } else {
            context.appName
        }

        val apkLocalPath = "$filePath/$apkName.apk"

        downloadUpdateApkFilePath = apkLocalPath

        SPUtil.putBase(KEY_OF_SP_APK_PATH, downloadUpdateApkFilePath)

        FileDownloader.setup(context)

        val downloadTask = FileDownloader.getImpl().create(updateInfo.apkUrl)
                .setPath(apkLocalPath)
                .setAutoRetryTimes(2)
        /*downloadTask
                .setListener(object : FileDownloadListener() {
                    override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                        downloadStart()
                        if (totalBytes < 0) {
                            downloadTask.pause()
                        }
                    }

                    override fun connected(task: BaseDownloadTask, etag: String, isContinue: Boolean, soFarBytes: Int, totalBytes: Int) {
                        downloadStart()
                    }

                    override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                        downloading(soFarBytes.toLong(), totalBytes.toLong())
                        if (totalBytes < 0) {
                            downloadTask.pause()
                        }
                    }

                    override fun blockComplete(task: BaseDownloadTask) {}
                    override fun retry(task: BaseDownloadTask, ex: Throwable, retryingTimes: Int, soFarBytes: Int) {

                    }

                    override fun completed(task: BaseDownloadTask) {
                        downloadComplete()
                    }

                    override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                        //downloadUpdateApkFilePath.deleteFile()
                        //"$downloadUpdateApkFilePath.temp".deleteFile()
                        downloadByHttpUrlConnection(filePath, apkName)
                    }

                    override fun error(task: BaseDownloadTask, e: Throwable) {
                        //downloadUpdateApkFilePath.deleteFile()
                        // "$downloadUpdateApkFilePath.temp".deleteFile()
                        downloadByHttpUrlConnection(filePath, apkName)
                    }

                    override fun warn(task: BaseDownloadTask) {}
                }).start()*/
        downloadTask
                .addHeader("Accept-Encoding", "identity")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36")
                .setListener(object : FileDownloadSampleListener() {

                    override fun pending(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                        downloadStart()
                        if (totalBytes < 0) {
                            downloadTask.pause()
                        }
                    }

                    override fun connected(task: BaseDownloadTask, etag: String, isContinue: Boolean, soFarBytes: Int, totalBytes: Int) {
                        downloadStart()
                    }

                    override fun progress(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                        downloading(soFarBytes.toLong(), totalBytes.toLong())
                        if (totalBytes < 0) {
                            downloadTask.pause()
                        }
                    }

                    override fun paused(task: BaseDownloadTask?, soFarBytes: Int, totalBytes: Int) {
                        downloadByHttpUrlConnection(filePath, apkName)
                    }

                    override fun completed(task: BaseDownloadTask) {
                        downloadComplete()
                    }

                    override fun error(task: BaseDownloadTask, e: Throwable) {
                        // FileDownloader 下载失败后，再调用 FileDownloadUtil 下载一次
                        // FileDownloader 对码云或者阿里云上的apk文件会下载失败
                        // downloadError(e)
                        downloadByHttpUrlConnection(filePath, apkName)
                    }

                    override fun warn(task: BaseDownloadTask) {
                    }
                }).start()
    }

    /**
     * 使用 HttpUrlConnection 下载
     */
    private fun downloadByHttpUrlConnection(filePath: String, apkName: String?) {
        FileDownloadUtil.download(
                updateInfo.apkUrl,
                filePath,
                "$apkName.apk",
                onStart = { downloadStart() },
                onProgress = { current, total -> downloading(current, total) },
                onComplete = { downloadComplete() },
                onError = { downloadError(it) }
        )
    }

    /**
     * 开始下载逻辑
     */
    private fun downloadStart() {
        isDownloading = true
        UpdateAppUtils.downloadListener?.onStart()
        UpdateAppReceiver.send(context, 0)
    }

    /**
     * 下载中逻辑
     */
    private fun downloading(soFarBytes: Long, totalBytes: Long) {
        isDownloading = true
        var progress = (soFarBytes * 100.0 / totalBytes).toInt()
        if (progress < 0) progress = 0
        UpdateAppReceiver.send(context, progress)
        this@DownloadAppUtils.onProgress.invoke(progress)
        UpdateAppUtils.downloadListener?.onDownload(progress)
    }

    /**
     * 下载完成处理逻辑
     */
    private fun downloadComplete() {
        isDownloading = false
        this@DownloadAppUtils.onProgress.invoke(100)
        UpdateAppUtils.downloadListener?.onFinish()
        // 校验md5
        (updateInfo.config.needCheckMd5).yes {
            checkMd5(context)
        }.no {
            UpdateAppReceiver.send(context, 100)
        }
    }

    /**
     * 下载失败处理逻辑
     */
    private fun downloadError(e: Throwable) {
        isDownloading = false
        downloadUpdateApkFilePath.deleteFile()
        this@DownloadAppUtils.onError.invoke()
        UpdateAppUtils.downloadListener?.onError(e)
        UpdateAppReceiver.send(context, -1000)
    }

    /**
     * 校验Md5
     *  先获取本应用的MD5值，获取未安装应用的MD5.进行对比
     */
    private fun checkMd5(context: Context) {
        // 当前应用md5
        val localMd5 = SignMd5Util.getAppSignatureMD5()

        // 下载的apk 签名md5
        val apkMd5 = SignMd5Util.getSignMD5FromApk(File(downloadUpdateApkFilePath))

        // 校验结果回调
        UpdateAppUtils.md5CheckResultListener?.onResult(localMd5.equals(apkMd5, true))

        (localMd5.equals(apkMd5, true)).yes {
            UpdateAppReceiver.send(context, 100)
        }.no {
        }
    }
}