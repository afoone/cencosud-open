On mac:

https://api-developers.ecomm-stg.cencosud.com/v1/auth/apiKey


## 1st: Get the certificate
echo -n | openssl s_client -connect api-developers.ecomm-stg.cencosud.com:443 -servername api-developers.ecomm-stg.cencosud.com   | openssl x509 > /tmp/api-developers.ecomm-stg.cencosud.com.cert

certificate is:

```
-----BEGIN CERTIFICATE-----
MIID4DCCAsigAwIBAgIQbK3/e68y0unOSvs9c2SfADANBgkqhkiG9w0BAQsFADCB
rTELMAkGA1UEBhMCQ0gxFDASBgNVBAgTC1N3aXR6ZXJsYW5kMQ8wDQYDVQQHEwZH
ZW5ldmExDTALBgNVBAoTBFNJVEExFDASBgNVBAsMC0MmVFMgLSBDSVNPMSgwJgYD
VQQDEx9TSVRBIE5HRlcgQ2VydGlmaWNhdGUgQXV0aG9yaXR5MSgwJgYJKoZIhvcN
AQkBFhlzZWN1cml0eS5vZmZpY2VAc2l0YS5hZXJvMB4XDTIzMDIyODAwMDAwMFoX
DTIzMDkyMDIzNTk1OVowIzEhMB8GA1UEAwwYKi5lY29tbS1zdGcuY2VuY29zdWQu
Y29tMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4MKh6EKo9tFGKgT1
4EepcEi0SN+/gfaBWSSKExkl7Or8nO/7T7raOiCRGsfGHLXN0DXfQbJC9GBlHRsO
Q8vJSrUdJBz6Kud+G1YjcoXmq/kdUDOuJYlmgVXcfI4KxEbBJ6ntnZKNuLG773+X
cPL2HOUyNLzWQkuPVDV2UPTncnHNoPWNNgFpZYoDc5MwgQoBo/+e3t4p3NM5u3Pa
ADJOSH2+UAWh8GyS4KTmsN/R1pVt1TrmIR1Azz3K6ws+7MJgc9Kh29ofOPuqzYQG
fgtldDgE7ZmT/BBCDNbsqQ1watUIFW8HXAIANc86z5pxkOvqnf0zx+Cg9bfvyc3C
QDAv2QIDAQABo4GEMIGBMB0GA1UdDgQWBBSPV63ZSKe2hTEgg+qbC2gQZegB8jAj
BgNVHREEHDAaghgqLmVjb21tLXN0Zy5jZW5jb3N1ZC5jb20wDgYDVR0PAQH/BAQD
AgWgMB0GA1UdJQQWMBQGCCsGAQUFBwMBBggrBgEFBQcDAjAMBgNVHRMBAf8EAjAA
MA0GCSqGSIb3DQEBCwUAA4IBAQBE6WKVdoQHtlMh3lxqotw4n+hOHasIS3acY5iB
YvP5Nc4AJ+G3T0chkTyBgJiA3TGOGlFj9sMr+4zCH4lj6WutuMSXbZOqiwhsf4ju
G/wcZ5e5vMTmu7FI2wRkJUrVru9YXiTcVyPoBSWDUpSJloHkoefind3hr6dsvPQc
UEMaBuiSc9z/YDXFLW1qPv9DEO2UFHmgraFisc3f4F1WvPYomzblrFpgKZkaQ4/x
dP8jTSbhIOAbQyO4Hlaf7qMl/8GQfnH/lzk+d/YYbYnhH87FYyEgP3WFUTaUFwBx
7P0AfFpw5SuC7L+6eXSDHNmSghMIb7neReHegXRgJnv7iyls
-----END CERTIFICATE-----
```

## 2nd: find jdk path
/usr/libexec/java_home


in my case: 

```
~/desarrollo/moddo/rest-test-moddo-cencosud $ /usr/libexec/java_home
/Users/atienda/Library/Java/JavaVirtualMachines/jdk-17.0.2.jdk/Contents/Home
```

or with jenv:

```
echo $JAVA_HOME
~/.jenv/versions/1.8/jre/
```

so: 
```
/Users/atienda/.jenv/versions/1.8/jre/
```

## 3rd: import certificate

```
sudo keytool -import -alias cencosud -keystore "/Users/atienda/.jenv/versions/1.8/jre/lib/security/cacerts" -file /tmp/api-developers.ecomm-stg.cencosud.com.cert
```

> Remember the default keystore password is "changeit"