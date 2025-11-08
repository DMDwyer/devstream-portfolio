
@RestController
@RequestMapping("/insights")
public class InsightController {
    private final InsightService service;
    public InsightController(InsightService service){
        this.service = service;
    }

    @GetMapping
    public List<Insight> all() {
        return service.getAll();
    }

    @PostMapping
    public Insight create(@RequestBody Insight insight) {
        return service.save(insight);
    }
}