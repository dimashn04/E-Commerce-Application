package com.app.services;

import com.app.entites.*;
import com.app.repositories.*;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.IntStream;

@Service
public class DataSeedService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private RoleRepo roleRepo;
    @Autowired
    private AddressRepo addressRepo;
    @Autowired
    private CategoryRepo categoryRepo;
    @Autowired
    private ProductRepo productRepo;
    @Autowired
    private BankRepo bankRepo;
    @Autowired
    private CartRepo cartRepo;
    @Autowired
    private OrderRepo orderRepo;
    @Autowired
    private PaymentRepo paymentRepo;
    @Autowired
    private CartItemRepo cartItemRepo;
    @Autowired
    private OrderItemRepo orderItemRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker(new Locale("in-ID"));

    @Transactional
    public void seedData() {
        seedAddresses();
        seedUsers();
        seedCategories();
        seedProducts();
        seedCarts();
        seedOrders();
        seedPayments();
        seedCartItems();
        seedOrderItems();
    }

    private void seedAddresses() {
        if (addressRepo.count() == 0) {
            List<Address> addresses = IntStream.range(0, 10)
                    .mapToObj(i -> new Address(null,
                            faker.address().streetName(),
                            faker.company().name(),
                            faker.address().city(),
                            faker.address().state(),
                            faker.address().country(),
                            StringUtils.rightPad(faker.address().zipCode(), 6, '0'),
                            new ArrayList<>()))
                    .toList();
            addressRepo.saveAll(addresses);
        }
    }

    private void seedUsers() {
        if (userRepo.count() == 0) {
            List<Role> roles = roleRepo.findAll();
            List<Address> addresses = addressRepo.findAll();

            List<User> users = IntStream.range(0, 10).mapToObj(i -> {
                User user = new User();
                String firstName;
                do {
                    firstName = faker.name().firstName();
                } while (firstName.length() < 5 || firstName.length() > 20);
                user.setFirstName(firstName);

                String lastName;
                do {
                    lastName = faker.name().lastName();
                } while (lastName.length() < 5 || lastName.length() > 20);
                user.setLastName(lastName);
                user.setMobileNumber(faker.numerify("##########"));
                user.setEmail(faker.internet().safeEmailAddress());
                user.setPassword(passwordEncoder.encode("password"));

                if (!roles.isEmpty()) {
                    user.setRoles(new HashSet<>(Collections.singletonList(roles.get(0))));
                } else {
                    user.setRoles(new HashSet<>());
                }

                if (!addresses.isEmpty()) {
                    user.setAddresses(Collections.singletonList(addresses.get(i % addresses.size())));
                } else {
                    user.setAddresses(new ArrayList<>());
                }

                return user;
            }).toList();

            userRepo.saveAll(users);
        }
    }

    private void seedCategories() {
        if (categoryRepo.count() == 0) {
            List<Category> categories = IntStream.range(0, 10)
                    .mapToObj(i -> {
                        String categoryName;
                        do {
                            categoryName = faker.commerce().department();
                        } while (categoryName.length() < 5);
                        return new Category(null, categoryName, new ArrayList<>());
                    })
                    .toList();
            categoryRepo.saveAll(categories);
        }
    }

    private void seedProducts() {
        if (productRepo.count() == 0) {
            List<Category> categories = categoryRepo.findAll();
            List<Product> products = IntStream.range(0, 10)
                    .mapToObj(i -> new Product(null, faker.commerce().productName(), faker.internet().image(),
                            faker.lorem().sentence(), faker.number().numberBetween(10, 100),
                            faker.number().randomDouble(2, 10, 500), faker.number().randomDouble(2, 0, 50),
                            faker.number().randomDouble(2, 5, 450), categories.get(i % categories.size()),
                            new ArrayList<>(), new ArrayList<>()))
                    .toList();
            productRepo.saveAll(products);
        }
    }

    private void seedCarts() {
        if (cartRepo.count() == 0) {
            List<User> users = userRepo.findAll();
            List<Cart> carts = users.stream().map(user -> {
                double totalPrice = faker.number().randomDouble(2, 10, 500);
                return new Cart(null, user, new ArrayList<>(), totalPrice);
            }).toList();
            cartRepo.saveAll(carts);
        }
    }

    private void seedOrders() {
        if (orderRepo.count() == 0) {
            List<User> users = userRepo.findAll();
            List<Order> orders = users.stream().map(user -> new Order(null, user.getEmail(), new ArrayList<>(),
                    LocalDate.now(), null, faker.number().randomDouble(2, 50, 1000), "Pending"))
                    .toList();
            orderRepo.saveAll(orders);
        }
    }

    private void seedPayments() {
        if (paymentRepo.count() == 0) {
            List<Order> orders = orderRepo.findAll();
            List<Payment> payments = orders.stream().map(order -> {
                Bank bank = bankRepo.findByName("BCA");
                Payment payment = new Payment(null, order, "Bank Transfer", bank);
                order.setPayment(payment);
                return payment;
            }).toList();
            paymentRepo.saveAll(payments);
        }
    }

    private void seedCartItems() {
        if (cartItemRepo.count() == 0) {
            List<Cart> carts = cartRepo.findAll();
            List<Product> products = productRepo.findAll();

            List<CartItem> cartItems = carts.stream()
                    .flatMap(cart -> IntStream.range(0, 3) // Setiap cart berisi 3 item
                            .mapToObj(i -> {
                                Product product = products.get(i % products.size());
                                int quantity = faker.number().numberBetween(1, 5);
                                double discount = faker.number().randomDouble(2, 0, 20);
                                double productPrice = product.getPrice() * quantity * (1 - discount / 100);
                                return new CartItem(null, cart, product, quantity, discount, productPrice);
                            }))
                    .toList();

            cartItemRepo.saveAll(cartItems);
        }
    }

    private void seedOrderItems() {
        if (orderItemRepo.count() == 0) {
            List<Order> orders = orderRepo.findAll();
            List<Product> products = productRepo.findAll();

            List<OrderItem> orderItems = orders.stream()
                    .flatMap(order -> IntStream.range(0, 3) // Setiap order berisi 3 item
                            .mapToObj(i -> {
                                Product product = products.get(i % products.size());
                                int quantity = faker.number().numberBetween(1, 5);
                                double discount = faker.number().randomDouble(2, 0, 20);
                                double orderedProductPrice = product.getPrice() * quantity * (1 - discount / 100);
                                return new OrderItem(null, product, order, quantity, discount, orderedProductPrice);
                            }))
                    .toList();

            orderItemRepo.saveAll(orderItems);
        }
    }
}