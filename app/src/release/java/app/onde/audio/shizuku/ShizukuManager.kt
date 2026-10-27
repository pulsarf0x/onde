package app.onde.audio.shizuku

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Play release: standard Android consent only, no privileged capture or Shizuku SDK. */
object ShizukuManager {
    enum class Status { NOT_INSTALLED, NOT_RUNNING, NO_PERMISSION, READY }
    val status: StateFlow<Status> = MutableStateFlow(Status.NOT_INSTALLED).asStateFlow()
    val privileged: StateFlow<IPrivilegedService?> = MutableStateFlow<IPrivilegedService?>(null).asStateFlow()
    fun init(context: Context) = Unit
    fun refresh() = Unit
    fun requestPermission() = Unit
    fun bind() = Unit
    suspend fun awaitPrivileged(timeoutMs: Long = 3000): IPrivilegedService? = null
    suspend fun setupPermissions(): String = "Cette version utilise les autorisations Android standard."
    fun projectionPreApproved(context: Context): Boolean = false
}
