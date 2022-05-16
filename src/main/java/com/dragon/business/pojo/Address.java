package com.dragon.business.pojo;

/**
 * @author xuejingbao
 * @create 2021-12-22 10:13
 */
public class Address {

    private String ip;
    private Integer port;

    public Address() {
    }

    public Address(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Address{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
