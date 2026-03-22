package com.project.fridgemate.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.widget.Filter
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.project.fridgemate.data.remote.api.NominatimApi
import com.project.fridgemate.data.remote.dto.AddressDto
import com.project.fridgemate.data.remote.dto.NominatimResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LocationAutoCompleteHelper(
    private val autoCompleteTextView: AutoCompleteTextView,
    private val scope: CoroutineScope
) {
    private var searchJob: Job? = null
    private val suggestions = mutableListOf<NominatimResult>()
    private var selectedAddress: AddressDto? = null

    // Adapter that bypasses ArrayAdapter's built-in filter so our API results are never hidden
    private val adapter = NoFilterAdapter(autoCompleteTextView.context)

    init {
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.threshold = 2

        autoCompleteTextView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                selectedAddress = null
            }
            override fun afterTextChanged(s: Editable?) {
                val query = s?.toString()?.trim() ?: return
                if (query.length < 2) return
                searchJob?.cancel()
                searchJob = scope.launch {
                    delay(500)
                    fetchSuggestions(query)
                }
            }
        })

        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val result = suggestions.getOrNull(position) ?: return@setOnItemClickListener
            val city = result.address?.city ?: result.address?.town ?: result.address?.village
            selectedAddress = AddressDto(
                fullAddress = result.displayName,
                city = city,
                country = result.address?.country,
                lat = result.lat.toDoubleOrNull(),
                lng = result.lon.toDoubleOrNull()
            )
            // Set text without triggering filter again
            autoCompleteTextView.setText(result.displayName, false)
        }
    }

    fun getSelectedAddress(): AddressDto? = selectedAddress

    private suspend fun fetchSuggestions(query: String) {
        try {
            val results = withContext(Dispatchers.IO) {
                NominatimApi.instance.search(query, "json", 5, 1)
            }
            suggestions.clear()
            suggestions.addAll(results)
            val names = results.map { it.displayName }
            adapter.setItems(names)
            if (autoCompleteTextView.isFocused && names.isNotEmpty()) {
                autoCompleteTextView.showDropDown()
            }
        } catch (_: Exception) {
            // Silently ignore errors in autocomplete
        }
    }

    /** ArrayAdapter subclass whose filter never hides items — we control the list via the API. */
    private class NoFilterAdapter(context: Context) :
        ArrayAdapter<String>(context, android.R.layout.simple_dropdown_item_1line, mutableListOf()) {

        private val items = mutableListOf<String>()

        fun setItems(newItems: List<String>) {
            items.clear()
            items.addAll(newItems)
            clear()
            addAll(newItems)
            notifyDataSetChanged()
        }

        override fun getFilter(): Filter = object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults =
                FilterResults().apply { values = items; count = items.size }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }
}
