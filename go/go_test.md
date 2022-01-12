# go 单元测试

    // 单元测试, 用于测试代码逻辑是否正确
    // 单个测试用例
    func TestIntToHex(t *testing.T) {
    got := IntToHex(1638868120)
    want := []byte{0, 0, 0, 0, 97, 175, 36, 152}
    if !reflect.DeepEqual(got, want) {
    t.Errorf("want: %v got:%v",want, got)
    }
    }
    // 测试组
    /*func TestIntToHex(t *testing.T) {
    
        // 定义参数和返回值的结构体
        type testUnit struct {
            input int64
            want  []byte
        }
    
        // 编造参数和返回值的结构体并放到数组中
        testUnits := []testUnit{
            {input: 1, want: []byte{0, 0, 0, 0, 0, 0, 0, 1}},
            {input: 2, want: []byte{0, 0, 0, 0, 0, 0, 0, 2}},
            {input: 3, want: []byte{0, 0, 0, 0, 0, 0, 0, 3}},
            {input: 4, want: []byte{0, 0, 0, 0, 0, 0, 0, 4}},
        }
    
        // 循环测试组进行测试
        for i, test := range testUnits {
            t.Run(strconv.Itoa(i), func(t *testing.T) {
                got := IntToHex(test.input)
                if !reflect.DeepEqual(got, test.want) {
                    t.Errorf("want: %v got:%v", test.want, got)
                }
            })
        }
    
    }*/
    
    
    // 性能基准测试
    func BenchmarkIntToHex(b *testing.B) {
    
        for i := 0; i < b.N; i++ {
            IntToHex(int64(i))
        }
    
    }



### go test

    在项目中的某一个包下执行该命令, 可以自动运行该包下面所有的单元测试方法

### go test -v

    可以用来查看之前的单元测试执行的详细信息

### go test -run=FuncName/1

    如果使用测试组模式进行, 搭配 go test -v 使用, 可以用来指定函数名为 FuncName 的测试步骤为 1 的那次记录再次进行执行

### go test -cover

    用于查看当前包下的单元测试代码覆盖率

### go test -bench=IntToHex

    性能基准测试

    结果讲解
    PS D:\dev\go\galaxy-coin\utils> go test -bench=IntToHex         // 执行命令
    goos: windows                                                   // Windows 系统
    goarch: amd64                                                   // 
    pkg: galaxy-coin/utils                                          // 所在包
    cpu: Intel(R) Core(TM) i7-9700 CPU @ 3.00GHz                    // CPU 信息
    BenchmarkIntToHex-8     10743080               112.6 ns/op      // ***-8: 一共有八个核可以进行处理, 一共执行了 10743080 次, op = 次, 平均每次执行 112.6ns
    PASS
    ok      galaxy-coin/utils       1.450s                          // 总执行时间为 1.450s

### go test -bench=IntToHex -benchmem

    性能基准测试, 打印内存信息

    结果讲解
    goos: windows
    goarch: amd64
    pkg: galaxy-coin/utils
    cpu: Intel(R) Core(TM) i7-9700 CPU @ 3.00GHz
    BenchmarkIntToHex-8     10598337               111.3 ns/op           128 B/op          3 allocs/op          // 每次操作会使用 128B 的内存, 每次操作会进行 3次 的内存申请
    PASS
    ok      galaxy-coin/utils       1.415s
