package com.app.entites;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long paymentId;

	@OneToOne(mappedBy = "payment", cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	private Order order;

	@NotBlank
    @Pattern(regexp = "Bank Transfer", message = "Only 'Bank Transfer' is allowed as payment method")
    private String paymentMethod;

	@NotBlank
    @Pattern(regexp = "BCA|Mandiri|BRI|BNI", message = "Supported banks: BCA, Mandiri, BRI, BNI")
    private String bankName;

	@NotBlank
	@Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
	private String cardNumber;
}
