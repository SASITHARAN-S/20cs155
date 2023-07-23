package com.example.demo;

import org.springframework.web.bind.annotation.RestController;

@RestController

@Service
public class JohnDoeRailwayService {
    @Value("${john-doe-railway.api.register-url}")
    private String registerUrl;

    @Value("${john-doe-railway.api.auth-url}")
    private String authUrl;

    @Value("${john-doe-railway.api.trains-url}")
    private String trainsUrl;

    private String accessToken;

    @Autowired
    private RestTemplate restTemplate;

    public List<Train> getTrainsInNext12Hours() {
        List<Train> allTrains = fetchAllTrains();
        return filterTrainsInNext12Hours(allTrains);
    }

    // Method to fetch all trains from the John Doe Railway Server
    private List<Train> fetchAllTrains() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Train[]> response = restTemplate.exchange(
                trainsUrl,
                HttpMethod.GET,
                entity,
                Train[].class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return Arrays.asList(response.getBody());
        } else {
            throw new RuntimeException("Failed to fetch train data from the John Doe Railway Server.");
        }
    }

    // Method to filter trains departing in the next 12 hours
    private List<Train> filterTrainsInNext12Hours(List<Train> allTrains) {
        long twelveHoursFromNow = System.currentTimeMillis() + 12 * 60 * 60 * 1000;
        return allTrains.stream()
                .filter(train -> getDepartureTimeInMillis(train) >= twelveHoursFromNow)
                .collect(Collectors.toList());
    }

    // Helper method to calculate the departure time in milliseconds
    private long getDepartureTimeInMillis(Train train) {
        return LocalDateTime.of(
                LocalDate.now(),
                LocalTime.of(train.getDepartureTime().getHours(), train.getDepartureTime().getMinutes())
        ).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() + train.getDelayedBy() * 60 * 1000;
    }
}
