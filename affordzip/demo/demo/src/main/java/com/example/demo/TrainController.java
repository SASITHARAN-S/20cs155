package com.example.demo;
@RestController
public class TrainController {
    @Autowired
    private JohnDoeRailwayService railwayService;

    @GetMapping("/trains")
    public List<Train> getTrains() {
        return railwayService.getTrainsInNext12Hours();
}
}
