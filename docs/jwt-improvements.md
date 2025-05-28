# JWT 实现改进记录

## 2024-01-30 修改

### 问题描述
1. 登录接口返回 Base64 解码错误：
   ```
   {"code":500,"message":"Illegal base64 character: '-'","data":null}
   ```
   原因：配置文件中的 JWT 密钥不是有效的 Base64 编码字符串。

2. JWT 签名密钥长度不足：
   ```
   {"code":500,"message":"The signing key's size is 240 bits which is not secure enough for the HS512 algorithm...","data":null}
   ```
   原因：使用 HS512 算法时，密钥长度必须 >= 512 bits。

### 解决方案
1. 修改 `JwtUtil.java`，使用 JJWT 提供的 `Keys.secretKeyFor()` 方法生成安全的密钥：
   ```java
   private final SecretKey secret = Keys.secretKeyFor(SignatureAlgorithm.HS512);
   ```

### 注意事项
1. 当前实现在每次应用重启时都会生成新的密钥，这会导致之前签发的 token 失效
2. 密钥直接在代码中生成，不支持配置和管理

### 后续改进建议

#### 密钥管理
1. 实现密钥持久化存储
   - 可以将生成的密钥保存到数据库或配置文件
   - 应用启动时优先读取已存储的密钥

2. 使用配置中心管理密钥
   - 考虑使用 Nacos、Apollo 等配置中心
   - 支持动态更新密钥

3. 实现密钥轮换机制
   - 定期自动更新密钥
   - 支持新旧密钥共存期
   - 平滑过渡，避免用户登录态丢失

#### 安全性增强
1. 添加 token 黑名单机制
   - 支持手动吊销指定 token
   - 用户修改密码后自动吊销旧 token

2. 完善 token 验证
   - 添加 token 过期时间验证
   - 支持 token 自动续期
   - 增加设备标识验证

3. 日志记录
   - 记录 token 的生成和验证日志
   - 记录异常情况和可疑操作

#### 性能优化
1. 添加缓存机制
   - 缓存活跃 token 的验证结果
   - 使用 Redis 等缓存系统

2. 考虑使用异步处理
   - 日志记录异步化
   - token 黑名单检查优化

### 参考资料
- [JWT JWA Specification (RFC 7518, Section 3.2)](https://tools.ietf.org/html/rfc7518#section-3.2)
- [JJWT Documentation](https://github.com/jwtk/jjwt)