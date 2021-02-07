# SpringBoot 单元测试

    @RunWith(SpringRunner.class)
    @SpringBootTest
    public class TestServiceTest extends TestCase {
    
        @Autowired
        private TestService testService;
    
        @Test
        public void testAaaa() {
            this.testService.aaaa();
        }
    }
    