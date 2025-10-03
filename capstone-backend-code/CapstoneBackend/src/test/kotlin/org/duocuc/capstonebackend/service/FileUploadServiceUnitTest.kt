package org.duocuc.capstonebackend.service

import org.junit.jupiter.api.Test
import org.duocuc.capstonebackend.util.nameToTitleCase

class FileUploadServiceUnitTest {
    @Test
    fun `splitFullNameFromExcel splits correctly when there are multiple last names`(){
        val fullName = "LEON MORALES IGNACIO FRANCISCO"
        val fullNameToTitleCase = fullName.nameToTitleCase()
        val (lastName, firstName) = FileUploadService().splitFullNameFromExcel(fullNameToTitleCase)
        assert(lastName == "Leon Morales")
        assert(firstName == "Ignacio Francisco")
    }

    @Test
    fun `splitFullNameFromExcel splits correctly when there is a single first name`(){
        val fullName = "BERTERO GONZALEZ MACARENA"
        val fullNameToTitleCase = fullName.nameToTitleCase()
        val (lastName, firstName) = FileUploadService().splitFullNameFromExcel(fullNameToTitleCase)
        assert(lastName == "Bertero Gonzalez")
        assert(firstName == "Macarena")
    }

}