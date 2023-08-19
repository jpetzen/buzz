package si.uni_lj.fe.tnuv.oleae.di

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import si.uni_lj.fe.tnuv.oleae.data.ClassificationReceiveManager
import si.uni_lj.fe.tnuv.oleae.data.ble.ClassificationBLEReceiveManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideBluetoothAdapter(@ApplicationContext context: Context): BluetoothAdapter {
        val manager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        return manager.adapter
    }

    @Provides
    @Singleton
    fun provideClassificationReceiveManager(
        @ApplicationContext context: Context,
        bluetoothAdapter: BluetoothAdapter
    ): ClassificationReceiveManager {
        return ClassificationBLEReceiveManager(bluetoothAdapter,context)
    }

}