package org.elnix.notes.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.runBlocking
import org.elnix.notes.data.AppDatabase
import org.elnix.notes.data.NoteRepository

/**
 * ContentProvider for sharing notes with other apps (specifically AlphaLM)
 * 
 * Authority: org.elnix.notes.provider
 * 
 * Query Methods:
 * - checkAppAvailable: Verify app is installed and available
 * - searchNotes: Search notes by query
 * - getNote: Get specific note by ID
 * - getAllNotes: Get all notes without filtering
 * - createNote: Create a new note
 */
class NotesContentProvider : ContentProvider() {

    companion object {
        const val AUTHORITY = "org.elnix.notes.provider"
        const val TAG = "NotesContentProvider"
        
        // Allowed caller package
        const val ALPHALLM_PACKAGE = "tech.alphallm.lucky"
        
        // Query methods
        const val METHOD_CHECK_APP = "checkAppAvailable"
        const val METHOD_SEARCH_NOTES = "searchNotes"
        const val METHOD_GET_NOTE = "getNote"
        const val METHOD_GET_ALL_NOTES = "getAllNotes"
        const val METHOD_CREATE_NOTE = "createNote"
        
        // Cursor columns
        private const val COLUMN_ID = "id"
        private const val COLUMN_TITLE = "title"
        private const val COLUMN_CONTENT = "content"
        private const val COLUMN_CREATED_AT = "created_at"
        private const val COLUMN_UPDATED_AT = "updated_at"
        private const val COLUMN_TAGS = "tags"
    }

    private var noteRepository: NoteRepository? = null
    private var prefs: SharedPreferences? = null

    override fun onCreate(): Boolean {
        return try {
            val context = context ?: return false
            // Initialize repository
            noteRepository = NoteRepository(AppDatabase.get(context).noteDao())
            // Initialize shared preferences for access control
            prefs = context.getSharedPreferences("notes_prefs", Context.MODE_PRIVATE)
            Log.d(TAG, "ContentProvider initialized successfully")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing ContentProvider", e)
            false
        }
    }

    override fun call(method: String, arg: String?, extras: Bundle?): Bundle? {
        Log.d(TAG, "=== CALL START ===")
        Log.d(TAG, "call() invoked: method=$method, arg=$arg, extras=$extras")
        return try {
            // For checkAppAvailable, skip all verification as it's just checking availability
            if (method != METHOD_CHECK_APP) {
                // Verify caller
                val callerPackage = getCallerPackage()
                if (!isAllowedCaller(callerPackage)) {
                    Log.w(TAG, "Unauthorized caller: $callerPackage")
                    return Bundle().apply { putString("error", "Unauthorized caller" ) }
                }

                // Check if access is allowed in settings
                if (!isAccessAllowed()) {
                    Log.w(TAG, "AlphaLM access is disabled in settings")
                    return Bundle().apply { putString("error", "Access denied by user") }
                }
            }

            when (method) {
                METHOD_CHECK_APP -> handleCheckApp()
                METHOD_SEARCH_NOTES -> {
                    Log.d(TAG, "Handling searchNotes with query from extras")
                    handleSearchNotes(extras?.getString("query") ?: "")
                }
                METHOD_GET_NOTE -> {
                    Log.d(TAG, "Handling getNote with noteId from extras")
                    handleGetNote(extras?.getString("noteId") ?: "")
                }
                METHOD_GET_ALL_NOTES -> {
                    Log.d(TAG, "Handling getAllNotes")
                    handleGetAllNotes()
                }
                METHOD_CREATE_NOTE -> {
                    Log.d(TAG, "Handling createNote with title from extras")
                    handleCreateNote(extras?.getString("title") ?: "", extras?.getString("content") ?: "")
                }
                else -> {
                    Log.w(TAG, "Unknown method: $method")
                    Bundle().apply { putString("error", "Unknown method: $method") }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling call", e)
            Bundle().apply { putString("error", "Internal error: ${e.message}") }
        }.also {
            Log.d(TAG, "=== CALL END === Returning: $it")
        }
    }

    /**
     * Verify that the caller is an allowed package
     */
    private fun isAllowedCaller(callerPackage: String?): Boolean {
        if (callerPackage == null) {
            Log.w(TAG, "Caller package is null")
            return false
        }
        val allowed = callerPackage == ALPHALLM_PACKAGE
        if (!allowed) {
            Log.w(TAG, "Caller package $callerPackage is not allowed (expecting $ALPHALLM_PACKAGE)")
        }
        return allowed
    }

    /**
     * Check if user has allowed AlphaLM access
     */
    private fun isAccessAllowed(): Boolean {
        return prefs?.getBoolean("allow_alphallm_access", false) ?: false
    }

    /**
     * Handle checkAppAvailable method - verify app is running
     */
    private fun handleCheckApp(): Bundle {
        Log.d(TAG, "handleCheckApp called")
        return Bundle().apply {
            putBoolean("available", true)
            putString("version", "1.0.0")
        }
    }

    /**
     * Handle searchNotes method - search notes by query
     */
    private fun handleSearchNotes(query: String): Bundle {
        Log.d(TAG, "[searchNotes] Starting search for query: '$query'")
        return try {
            if (query.isBlank()) {
                Log.w(TAG, "[searchNotes] Empty search query")
                return Bundle().apply { putString("error", "Empty search query") }
            }

            val repo = noteRepository ?: return Bundle().apply { 
                Log.e(TAG, "[searchNotes] Repository not initialized")
                putString("error", "Repository not initialized") 
            }

            Log.d(TAG, "[searchNotes] Starting database query...")
            // Search notes (this is a simple substring search)
            val notes = runBlocking {
                Log.d(TAG, "[searchNotes] Inside runBlocking, calling getAllNotes()")
                val allNotes = repo.getAllNotes()
                Log.d(TAG, "[searchNotes] getAllNotes() returned ${allNotes.size} notes")
                allNotes.filter { note ->
                    note.title.contains(query, ignoreCase = true) ||
                    note.desc.contains(query, ignoreCase = true)
                }
            }

            Log.d(TAG, "[searchNotes] Found ${notes.size} notes matching query: $query")

            val results = mutableListOf<Bundle>()
            for (note in notes) {
                val noteBundle = Bundle().apply {
                    putLong("id", note.id)
                    putString("title", note.title)
                    putString("content", note.desc)
                    putLong("created_at", note.createdAt.time)
                    putLong("updated_at", note.lastEdit)
                    putString("tags", note.tagIds.joinToString(","))
                }
                results.add(noteBundle)
            }

            Log.d(TAG, "[searchNotes] Returning ${results.size} result bundles")
            Bundle().apply {
                putParcelableArray("notes", results.toTypedArray())
                putInt("count", results.size)
            }
        } catch (e: Exception) {
            Log.e(TAG, "[searchNotes] Error searching notes", e)
            Bundle().apply { putString("error", "Search failed: ${e.message}") }
        }.also {
            Log.d(TAG, "[searchNotes] Returning bundle: $it")
        }
    }

    /**
     * Handle getAllNotes method - get all notes without filtering
     */
    private fun handleGetAllNotes(): Bundle {
        Log.d(TAG, "[getAllNotes] Starting get all notes")
        return try {
            val repo = noteRepository ?: return Bundle().apply { 
                Log.e(TAG, "[getAllNotes] Repository not initialized")
                putString("error", "Repository not initialized") 
            }

            Log.d(TAG, "[getAllNotes] Starting database query...")
            // Get all notes
            val notes = runBlocking {
                Log.d(TAG, "[getAllNotes] Inside runBlocking, calling getAllNotes()")
                repo.getAllNotes()
            }

            Log.d(TAG, "[getAllNotes] Found ${notes.size} notes")

            val results = mutableListOf<Bundle>()
            for (note in notes) {
                val noteBundle = Bundle().apply {
                    putLong("id", note.id)
                    putString("title", note.title)
                    putString("content", note.desc)
                    putLong("created_at", note.createdAt.time)
                    putLong("updated_at", note.lastEdit)
                    putString("tags", note.tagIds.joinToString(","))
                }
                results.add(noteBundle)
            }

            Log.d(TAG, "[getAllNotes] Returning ${results.size} result bundles")
            Bundle().apply {
                putParcelableArray("notes", results.toTypedArray())
                putInt("count", results.size)
            }
        } catch (e: Exception) {
            Log.e(TAG, "[getAllNotes] Error getting all notes", e)
            Bundle().apply { putString("error", "Get all notes failed: ${e.message}") }
        }.also {
            Log.d(TAG, "[getAllNotes] Returning bundle: $it")
        }
    }

    /**
     * Handle createNote method - create a new note
     */
    private fun handleCreateNote(title: String, content: String): Bundle {
        Log.d(TAG, "[createNote] Starting create note with title: '$title'")
        return try {
            if (title.isBlank()) {
                Log.w(TAG, "[createNote] Empty title")
                return Bundle().apply { putString("error", "Empty title") }
            }

            val repo = noteRepository ?: return Bundle().apply { 
                Log.e(TAG, "[createNote] Repository not initialized")
                putString("error", "Repository not initialized") 
            }

            Log.d(TAG, "[createNote] Creating new note...")
            // Create new note
            val newNote = org.elnix.notes.data.NoteEntity(
                title = title,
                desc = content,
                lastEdit = System.currentTimeMillis()
            )

            val noteId = runBlocking {
                Log.d(TAG, "[createNote] Inside runBlocking, calling upsert")
                repo.upsert(newNote)
            }

            Log.d(TAG, "[createNote] Note created with ID: $noteId")

            Bundle().apply {
                putLong("noteId", noteId)
                putString("title", title)
                putString("content", content)
                putLong("createdAt", System.currentTimeMillis())
            }
        } catch (e: Exception) {
            Log.e(TAG, "[createNote] Error creating note", e)
            Bundle().apply { putString("error", "Create note failed: ${e.message}") }
        }.also {
            Log.d(TAG, "[createNote] Returning bundle: $it")
        }
    }

    /**
     * Handle getNote method - get specific note by ID
     */
    private fun handleGetNote(noteId: String): Bundle {
        return try {
            if (noteId.isBlank()) {
                Log.w(TAG, "Empty note ID")
                return Bundle().apply { putString("error", "Empty note ID") }
            }

            val id = noteId.toLongOrNull() ?: run {
                Log.w(TAG, "Invalid note ID format: $noteId")
                return Bundle().apply { putString("error", "Invalid note ID format") }
            }

            val repo = noteRepository ?: return Bundle().apply {
                putString("error", "Repository not initialized")
            }

            val note = runBlocking { repo.getById(id) }

            if (note == null) {
                Log.w(TAG, "Note not found: $id")
                return Bundle().apply { putString("error", "Note not found") }
            }

            Log.d(TAG, "Retrieved note: ${note.title}")

            Bundle().apply {
                putLong("id", note.id)
                putString("title", note.title)
                putString("content", note.desc)
                putLong("created_at", note.createdAt.time)
                putLong("updated_at", note.lastEdit)
                putString("tags", note.tagIds.joinToString(","))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error retrieving note", e)
            Bundle().apply { putString("error", "Retrieval failed: ${e.message}") }
        }
    }

    /**
     * Get the package name of the caller
     */
    private fun getCallerPackage(): String? {
        return try {
            // Try the modern API first (API 29+)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                val callingPackage = callingPackage
                if (callingPackage != null) {
                    Log.d(TAG, "Using modern API - calling package: $callingPackage")
                    return callingPackage
                }
            }

            // Fallback to Binder approach
            val callingPid = android.os.Binder.getCallingPid()
            val callingUid = android.os.Binder.getCallingUid()
            Log.d(TAG, "Using Binder approach - PID: $callingPid, UID: $callingUid")

            val context = context ?: return null
            val pm = context.packageManager
            val packages = pm.getPackagesForUid(callingUid)
            val callerPackage = packages?.firstOrNull()
            Log.d(TAG, "Detected caller package: $callerPackage")
            callerPackage
        } catch (e: Exception) {
            Log.e(TAG, "Error getting caller package", e)
            null
        }
    }

    // Not implemented - not needed for query operations
    override fun query(
        uri: Uri,
        projection: Array<String>?,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): Cursor? = null

    // Not implemented - not needed for this use case
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    // Not implemented - not needed for this use case
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0

    // Not implemented - not needed for this use case
    override fun delete(
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): Int = 0

    // Not implemented - not needed for this use case
    override fun getType(uri: Uri): String? = null
}
