package com.mbakgun.things.data

/**
 * Created by burakakgun on 9.06.2019.
 */

enum class NearbyType {
    UPDATE, DELETE, GET_ALL, MESSAGE
}

data class NearbyMessage(val nearbyType: NearbyType, val value: String)
