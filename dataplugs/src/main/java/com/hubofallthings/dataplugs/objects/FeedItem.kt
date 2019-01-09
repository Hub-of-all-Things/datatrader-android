package com.hubofallthings.dataplugs.objects

import java.io.Serializable

data class FeedItem(
        val date: DateFeed,
        val source: String,
        val content: Content? = null,
        val location: Location? = null,
        val title: Title? = null,
        val types: Array<String>
): Serializable

data class DateFeed(
        val iso: String,
        val unix: Number
): Serializable

data class Location(val address: Address? = null,
                    val geo: LocationGeo? = null,
                    val tags: Array<String>? = null): Serializable

data class Title(
        val action: String? = null,
        val text: String,
        val subtitle: String? = null
): Serializable
data class Content(val text: String? = null,
                   val html: String? = null,
                   val media: Array<FeedMedia>? = null,
                   val nestedStructure : List<Map<String , Array<DataFeedNestedStructureItem>>>? = null
): Serializable
data class DataFeedNestedStructureItem(
        val content : String,
        val badge : String? = null,
        val types : Array<String>? = null
): Serializable

data class Address(
        val city: String? = null,
        val country: String? = null,
        val name: String? = null,
        val street: String? = null,
        val zip: String? = null
): Serializable

data class LocationGeo(val latitude: Double,
                       val longitude: Double): Serializable

data class FeedMedia(val thumbnail: String? = null,
                     val url: String? = null): Serializable


