package com.tanvir.training.actioninputsbatch03

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

val cityList = listOf("Dhaka", "Chittagong", "Rajshahi", "Khulna", "Cox's Bazar", "Sylhet", "Comilla", "Noakhali", "Borishal")
val busTypeEconomy = "Economy"
val busTypeBusiness = "Business"

@Entity(tableName = "tbl_schedule")
data class BusSchedule(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    @ColumnInfo(name = "bus_name")
    val busName: String,
    val from: String,
    val to: String,
    @ColumnInfo(name = "departure_date")
    val departureDate: String,
    @ColumnInfo(name = "departure_time")
    val departureTime: String,
    @ColumnInfo(name = "bus_type")
    val busType: String,
    var favorite: Boolean = false,
    var image: String?,
)

