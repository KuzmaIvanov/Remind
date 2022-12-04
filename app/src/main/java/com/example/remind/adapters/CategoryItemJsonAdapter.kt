package com.example.remind.adapters

import android.icu.util.Calendar
import com.example.remind.model.CalendarItem
import com.google.gson.*
import java.lang.reflect.Type
import java.util.*

class CategoryItemJsonAdapter: JsonSerializer<CalendarItem>, JsonDeserializer<CalendarItem> {
    override fun serialize(
        src: CalendarItem?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        val result = JsonObject()
        result.add("id", JsonPrimitive(src?.id))
        result.add("isEveryDay", JsonPrimitive(src?.isEveryDay))
        result.add("isEveryWeek", JsonPrimitive(src?.isEveryWeek))
        result.add("isOnceOnAParticularDay", JsonPrimitive(src?.isOnceOnAParticularDay))
        result.add("calendar", context?.serialize(src?.calendar?.time, Date::class.java))
        return result
    }

    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): CalendarItem {
        val jsonObject = json?.asJsonObject
        val id = jsonObject?.get("id")?.asInt
        val everyDay = jsonObject?.get("isEveryDay")?.asBoolean
        val everyWeek = jsonObject?.get("isEveryWeek")?.asBoolean
        val partDay = jsonObject?.get("isOnceOnAParticularDay")?.asBoolean
        val element = jsonObject?.get("calendar")
        val date = context?.deserialize(element, Date::class.java) as Date
        val calendar = Calendar.getInstance()
        calendar.time = date
        return CalendarItem(id!!, calendar, everyDay!!, everyWeek!!, partDay!!)
    }
}