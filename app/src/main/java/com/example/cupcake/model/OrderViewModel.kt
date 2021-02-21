package com.example.cupcake.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

private const val PRICE_PER_CUPCAKE = 2.00
private const val PRICE_FOR_SAME_DAY_PICKUP = 3.00

class OrderViewModel : ViewModel() {


    private val _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> = _quantity

    private val _flavour = MutableLiveData<String>()
    val flavour: LiveData<String> = _flavour

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> = _date

    private val _price = MutableLiveData<Double>()

    /**
     * Use Transformations.map() method to format the price to use the local currency.
     */
    val price: LiveData<String> = Transformations.map(_price) {
        NumberFormat.getCurrencyInstance().format(it)
    }


    fun setQuantity(numberOfCupCakes: Int) {
        _quantity.value = numberOfCupCakes
        updatePrice()
    }

    fun setFlavour(desiredFlavour: String) {
        _flavour.value = desiredFlavour
    }

    fun setDate(pickupDate: String) {
        _date.value = pickupDate
        updatePrice()
    }

    fun hasNoFlavourSet(): Boolean {
        return _flavour.value.isNullOrEmpty()
    }

    fun hasNoDateSet(): Boolean {
        return _date.value.isNullOrEmpty()
    }

    private fun updatePrice() {

        /**
         * The elvis operator (?:) means that if the expression on the left is not null,
         * then use it. Otherwise if the expression on the left is null,
         * then use the expression to the right of the elvis operator (which is 0 in this case).
         */
        var calculatedPrice = (_quantity.value ?: 0) * PRICE_PER_CUPCAKE
        // If the user selected the first option (today) for pickup, add the surcharge
        if (_date.value == dateOptions[0]) {
            calculatedPrice += PRICE_FOR_SAME_DAY_PICKUP
        }
        _price.value = calculatedPrice
    }

    private fun getPickupOptions(): List<String> {

        val options = mutableListOf<String>()
        val formatter = SimpleDateFormat("E MMM d", Locale.getDefault())
        val calender = Calendar.getInstance()

        // Create a list of dates starting with the current date and the following 3 dates
        /**
         * This repeat block will format a date, add it to the list of date options,
         * and then increment the calendar by 1 day.
         */

        repeat(4) {
            options.add(formatter.format(calender.time))
            calender.add(Calendar.DATE, 1)
        }
        return options
    }

    val dateOptions = getPickupOptions()

    fun resetOrder() {
        _quantity.value = 0
        _flavour.value = ""
        _date.value = dateOptions[0]
        _price.value = 0.0

    }

    init {
        resetOrder()
    }

}