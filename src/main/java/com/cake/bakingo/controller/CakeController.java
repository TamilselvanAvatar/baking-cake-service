package com.cake.bakingo.controller;

import java.util.Date;
import java.util.Set;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.cake.bakingo.bean.Cake;
import com.cake.bakingo.service.CakeService;

@Controller
public class CakeController {

	@Autowired
	private CakeService cakeService;

	private String homePage = "placeOrder";

	@GetMapping(value = "/showCakeOrderForm")
	public String showCakeOrderForm(@ModelAttribute("cake") Cake cake) {

		return homePage;
	}

	@PostMapping(value = "/orderStatus")
	public String getOrderStatus(@Valid @ModelAttribute("cake") Cake cake, BindingResult result, ModelMap map) {

		if (result.hasErrors()) {
			return homePage;
		}

		cake.setFlavorRate(CakeService.flavorList.get(cake.getFlavor()));

		double price = 0.0;

		try {
			price = cake.getSelectedcake() + cake.getFlavorRate() + cake.getIncludeCandles();
		} catch (NullPointerException e) {
			price = cake.getSelectedcake() + cake.getFlavorRate();
		}

		try {
			price += cake.getIncludeinscription();
		} catch (NullPointerException e) {
		}

		cake.setPrice(price);

		int orderId = cakeService.addOrder(cake);

		map.addAttribute("cake", cake);
		map.addAttribute("indianPrice", cake.getPrice() * 75.0);
		map.addAttribute("orderId", orderId);
		map.addAttribute("orderDate", new Date().toString());

		return orderId >= 1000 ? "orderStatus" : homePage;
	}

	@ModelAttribute("flavorList")
	public Set<String> populateFillingList() {
		return CakeService.flavorList.keySet();
	}

}
