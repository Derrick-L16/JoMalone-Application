package com.example.jomalonemobileapplication

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.jomalonemobileapplication.core.data.dao.CartItemDao
import com.example.jomalonemobileapplication.core.data.dao.CustomizationDao
import com.example.jomalonemobileapplication.core.data.dao.DeliveryAddressDao
import com.example.jomalonemobileapplication.core.data.dao.OrderDao
import com.example.jomalonemobileapplication.core.data.dao.PaymentMethodDao
import com.example.jomalonemobileapplication.core.data.entity.CartItemEntity
import com.example.jomalonemobileapplication.core.data.entity.CustomizationEntity
import com.example.jomalonemobileapplication.core.data.entity.DeliveryAddressEntity
import com.example.jomalonemobileapplication.core.data.entity.OrderEntity
import com.example.jomalonemobileapplication.core.data.entity.PaymentMethodEntity
import com.example.jomalonemobileapplication.feature.login.data.UserDao
import com.example.jomalonemobileapplication.feature.login.data.UserEntity

@Database(
    entities = [
        CartItemEntity::class,
        OrderEntity::class,
        PaymentMethodEntity::class,
        DeliveryAddressEntity::class,
        CustomizationEntity::class,
        UserEntity :: class
    ],
    version = 8,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cartItemDao(): CartItemDao
    abstract fun orderDao(): OrderDao
    abstract fun paymentMethodDao(): PaymentMethodDao
    abstract fun deliveryAddressDao(): DeliveryAddressDao
    abstract fun customizationDao(): CustomizationDao
    abstract fun userDao(): UserDao


    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { Instance = it }
            }
        }
    }
}
