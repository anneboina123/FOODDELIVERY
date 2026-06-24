package com.fooddelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {
    private String addressLine;
    private String city;
    private String pincode;
    private String addressType = "Home";
    private Boolean isDefault = false;
}
