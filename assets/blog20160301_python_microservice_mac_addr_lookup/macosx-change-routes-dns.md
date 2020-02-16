


__1) Check current Network Routes___


The current Interface used (Wi-Fi) to get Internet access is `en0` and the current routes are:

```bash
$ netstat -rn
Routing tables

Internet:
Destination        Gateway            Flags        Refs      Use   Netif Expire
default            192.168.1.1        UGSc           41        0     en0
127                127.0.0.1          UCS             1        0     lo0
127.0.0.1          127.0.0.1          UH              2   644937     lo0
169.254            link#4             UCS             1        0     en0
192.168.1          link#4             UCS             7        0     en0
192.168.1.1/32     link#4             UCS             4        0     en0
192.168.1.1        8c:c:a3:35:34:8f   UHLWIir         5        0     en0   1192
192.168.1.43/32    link#4             UCS             2        0     en0
192.168.1.43       14:10:9f:e5:6e:cf  UHLWIi          2        1     lo0
192.168.1.255      ff:ff:ff:ff:ff:ff  UHLWbI          1        1     en0
255.255.255.255/32 link#4             UCS             2        0     en0
```

After plugged the new `en6` Interface, the routes are below and don't have Internet access:
```bash
$ netstat -rn
Routing tables

Internet:
Destination        Gateway            Flags        Refs      Use   Netif Expire
default            172.16.42.1        UGSc            6        0     en6
default            192.168.1.1        UGScI          11        0     en0
127                127.0.0.1          UCS             1        0     lo0
127.0.0.1          127.0.0.1          UH              2   644937     lo0
169.254            link#10            UCS             1        0     en6
169.254            link#4             UCSI            1        0     en0
172.16.42/24       link#10            UCS             2        0     en6
172.16.42.1/32     link#10            UCS             2        0     en6
172.16.42.1        0:c0:ca:8d:a7:b7   UHLWIir         9       12     en6   1200
172.16.42.191/32   link#10            UCS             2        0     en6
172.16.42.255      ff:ff:ff:ff:ff:ff  UHLWbI          1        8     en6
192.168.1          link#4             UCS             4        0     en0
192.168.1.1/32     link#4             UCS             4        0     en0
192.168.1.1        8c:c:a3:35:34:8f   UHLWIir        13       16     en0   1197
192.168.1.43/32    link#4             UCS             2        0     en0
192.168.1.43       14:10:9f:e5:6e:cf  UHLWIi          1        1     lo0
192.168.1.255      ff:ff:ff:ff:ff:ff  UHLWbI          1        9     en0
255.255.255.255/32 link#10            UCS             1        0     en6
255.255.255.255/32 link#4             UCSI            1        0     en0
```

__2) Update Network Routes and DNS__


Just remove the problematic route. In this case I want to get Internet access by `en0` (Wi-Fi) Interface. 
```bash
$ sudo route -n delete -net default 172.16.42.1
delete net default: gateway 172.16.42.1 
```
Now, overwrite/update the existing route associated to `en0`.
```bash
$ sudo route -n add -net default 192.168.1.1
add net default: gateway 192.168.1.1
```

Add DNS to associated to `en0`. Before get the name of network service.
```bash
$ networksetup -listnetworkserviceorder
An asterisk (*) denotes that a network service is disabled.
(1) Bluetooth PAN
(Hardware Port: Bluetooth PAN, Device: en3)

(2) Android
(Hardware Port: Modem (usbmodem1d112), Device: usbmodem1d112)

(3) Aquaris_E5_FHD
(Hardware Port: Ethernet (en5), Device: en5)

(4) mac-eth0
(Hardware Port: Thunderbolt Ethernet, Device: en4)

(5) AX88x72A
(Hardware Port: AX88x72A, Device: en6)

(6) Wi-Fi
(Hardware Port: Wi-Fi, Device: en0)
```

Now, add DNS.
```
$ sudo networksetup -setdnsservers Wi-Fi 8.8.8.8
```
Check if DNS was updated.
```bash
$ networksetup -getdnsservers Wi-Fi
8.8.8.8
```

__3) Check final Network Routes and Internet access__

Now, you should get Internet access.

The final Network Routes are:
```bash
$ netstat -rn
Routing tables

Internet:
Destination        Gateway            Flags        Refs      Use   Netif Expire
default            192.168.1.1        UGSc            1        0     en0
default            192.168.1.1        UGScI         101        0     en0
127                127.0.0.1          UCS             1        0     lo0
127.0.0.1          127.0.0.1          UH              2   644937     lo0
169.254            link#10            UCS             1        0     en6
169.254            link#4             UCSI            1        0     en0
172.16.42/24       link#10            UCS             2        0     en6
172.16.42.1/32     link#10            UCS             2        0     en6
172.16.42.1        0:c0:ca:8d:a7:b7   UHLWIir         4     2768     en6   1199
172.16.42.191/32   link#10            UCS             1        0     en6
172.16.42.255      link#10            UHLWbI          1      101     en6
192.168.1          link#4             UCS             4        0     en0
192.168.1.1/32     link#4             UCS             2        0     en0
192.168.1.1        8c:c:a3:35:34:8f   UHLWIir       104       16     en0   1199
192.168.1.32       link#4             UHLWIi          1        1     en0
192.168.1.43/32    link#4             UCS             2        0     en0
192.168.1.43       14:10:9f:e5:6e:cf  UHLWIi          1        1     lo0
192.168.1.100      link#4             UHLWIi          1        1     en0
192.168.1.255      link#4             UHLWbI          1      102     en0
255.255.255.255/32 link#10            UCS             2        0     en6
255.255.255.255/32 link#4             UCSI            1        0     en0
255.255.255.255    link#10            UHLWbI          1       80     en6
```

__4) Setting the configuration permanently__

The DNS isn't necessary  (is optional) to be added, just remove and add network routes.

```bash
$ sudo route -n delete -net default 172.16.42.1; sudo route -n add -net default 192.168.1.1; sudo networksetup -setdnsservers Wi-Fi 8.8.8.8; netstat -rn
```
$ sudo route -n add -net 172.16.42.0/24 192.168.1.1 -ifp en0

