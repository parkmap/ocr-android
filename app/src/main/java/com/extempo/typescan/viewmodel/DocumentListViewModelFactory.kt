package com.extempo.typescan.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.extempo.typescan.model.repository.DocumentRepository

@Suppress("UNCHECKED_CAST")
class DocumentListViewModelFactory(private val documentRepository: DocumentRepository): ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return DocumentListViewModel(documentRepository) as T
    }
}