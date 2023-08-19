package si.uni_lj.fe.tnuv.oleae.data

import kotlinx.coroutines.flow.MutableSharedFlow
import si.uni_lj.fe.tnuv.oleae.util.Resource


interface ClassificationReceiveManager {

    val data: MutableSharedFlow<Resource<ClassificationResult>>

    fun reconnect()

    fun disconnect()

    fun startReceiving()

    fun closeConnection()

}