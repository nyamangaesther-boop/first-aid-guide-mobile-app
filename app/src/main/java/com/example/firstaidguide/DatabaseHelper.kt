package com.example.firstaidguide

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 8 // Incremented to refresh mappings after fixing file extensions
        private const val DATABASE_NAME = "FirstAid.db"
        private const val TABLE_INSTRUCTIONS = "instructions"
        private const val KEY_ID = "id"
        private const val KEY_TITLE = "title"
        private const val KEY_CONTENT = "content"
        private const val KEY_WARNING = "warning"
        private const val KEY_IMAGE = "image_res_name"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE " + TABLE_INSTRUCTIONS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," 
                + KEY_TITLE + " TEXT,"
                + KEY_CONTENT + " TEXT,"
                + KEY_WARNING + " TEXT,"
                + KEY_IMAGE + " TEXT" + ")")
        db?.execSQL(createTable)
        insertInitialData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS " + TABLE_INSTRUCTIONS)
        onCreate(db)
    }

    private fun insertInitialData(db: SQLiteDatabase?) {
        val data = listOf(
            InstructionData("Burns", 
                "1. Cool with cold running water for 20 minutes.\n2. Remove jewellery near the area.\n3. Lay kitchen film/plastic wrap lengthways over the burn.\n4. Use a sterile, non-fluffy dressing.\n5. Reassure the casualty.", 
                "DO NOT use ice or butter. DO NOT pop blisters. DO NOT remove clothing stuck to skin. DO NOT use fluffy dressings.", "img_burns"),
            InstructionData("Bleeding", 
                "1. Apply firm, steady pressure with hands.\n2. Apply a clean dressing and press firmly.\n3. If bleeding continues, apply a tourniquet 2-3 inches above the wound.", 
                "If an object is embedded, DO NOT remove it. Apply pressure around it.", "bleeding"),
            InstructionData("CPR", 
                "1. Check vital signs and responsiveness.\n2. Call 999 or 911 immediately.\n3. Check breathing.\n4. Perform CPR (hard and fast compressions).\n5. Give rescue breaths if trained.\n6. Once breathing, turn them on their side.", 
                "Continue until professional help arrives or an AED is ready.", "img_cpr"),
            InstructionData("Choking", 
                "1. Encourage the person to cough it out.\n2. Give 5 sharp back blows between shoulder blades.\n3. Give 5 abdominal thrusts (Heimlich).\n4. Repeat 5 and 5 until the object is forced out.", 
                "Call emergency services if they are still choking. DO NOT use abdominal thrusts on infants.", "chocking"),
            InstructionData("Fractures", 
                "1. Stop any bleeding.\n2. Immobilize the injured area.\n3. Apply an ice pack to limit swelling.\n4. Reassure the person.", 
                "DO NOT try to realign the bone. DO NOT move the person if neck/back injury is suspected.", "img_fractures"),
            InstructionData("Nosebleeds", 
                "1. Sit upright and TILT FORWARD.\n2. Pinch the soft part of the nose for 10-15 mins.\n3. Breathe through the mouth.\n4. Apply ice to the bridge of the nose.", 
                "DO NOT TILT BACK; this causes blood to go down the throat.", "img_nosebleed"),
            InstructionData("Poisoning", 
                "1. If in eyes/skin: Rinse with water for 15-20 mins.\n2. If inhaled: Get fresh air immediately.\n3. If swallowed medicine: Do not give anything by mouth.\n4. If swallowed non-food: Drink a small amount of milk/water.\n5. Call Poison Control: 1-800-222-1222.", 
                "DO NOT induce vomiting. If person collapses, call 911/999 immediately.", "img_poisoning"),
            InstructionData("Snake Bite", 
                "1. Stay calm and move away from the snake.\n2. Keep the bitten limb still.\n3. Remove tight clothing/jewelry.\n4. Lay the patient on their left side.\n5. Rush to a health facility immediately.", 
                "DO NOT panic. DO NOT cut, wash, or suck the wound. DO NOT use a tourniquet.", "img_snakebite"),
            InstructionData("Electric Shock",
                "1. Do not touch the person until they are clear of the power source.\n2. Turn off the electricity at the mains if possible.\n3. Call 999 immediately.\n4. Once safe, check breathing and start CPR if needed.\n5. Cover any burned areas with sterile gauze.",
                "Stay at least 20 feet away if high-voltage lines are involved.", "electric_shock"),
            InstructionData("Fainting",
                "1. Lay the person on their back.\n2. Elevate their legs about 12 inches above heart level.\n3. Loosen tight clothing (collars/belts).\n4. Check for breathing; if not breathing, start CPR.\n5. If they don't regain consciousness within 1 minute, call 999.",
                "DO NOT give the person anything to eat or drink until fully conscious.", "img_fainting")
        )

        data.forEach { item ->
            val values = ContentValues().apply {
                put(KEY_TITLE, item.title)
                put(KEY_CONTENT, item.content)
                put(KEY_WARNING, item.warning)
                put(KEY_IMAGE, item.imageResName)
            }
            db?.insert(TABLE_INSTRUCTIONS, null, values)
        }
    }

    fun getInstruction(title: String): Instruction? {
        val db = this.readableDatabase
        val cursor = db.query(TABLE_INSTRUCTIONS, null, "$KEY_TITLE=?", arrayOf(title), null, null, null)
        return cursor.use { 
            if (it.moveToFirst()) {
                Instruction(
                    it.getString(it.getColumnIndexOrThrow(KEY_TITLE)),
                    it.getString(it.getColumnIndexOrThrow(KEY_CONTENT)),
                    it.getString(it.getColumnIndexOrThrow(KEY_WARNING)),
                    it.getString(it.getColumnIndexOrThrow(KEY_IMAGE))
                )
            } else null
        }
    }

    fun searchInstructions(query: String): List<Instruction> {
        val list = mutableListOf<Instruction>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_INSTRUCTIONS WHERE $KEY_TITLE LIKE ?", arrayOf("%$query%"))
        cursor.use {
            if (it.moveToFirst()) {
                do {
                    list.add(Instruction(
                        it.getString(it.getColumnIndexOrThrow(KEY_TITLE)),
                        it.getString(it.getColumnIndexOrThrow(KEY_CONTENT)),
                        it.getString(it.getColumnIndexOrThrow(KEY_WARNING)),
                        it.getString(it.getColumnIndexOrThrow(KEY_IMAGE))
                    ))
                } while (it.moveToNext())
            }
        }
        return list
    }
}

data class InstructionData(val title: String, val content: String, val warning: String, val imageResName: String)
data class Instruction(val title: String, val content: String, val warning: String, val imageResName: String?)
