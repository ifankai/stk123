# coding=UTF-8

import time
import urllib.request as ur
import json as _json

class K:
    date = ''
    open = 0.0
    high = 0.0
    before = {}

    def __init__(self): {}

    def __init__(self,date,open,high):
        self.date = date
        self.open = open
        self.high = high

    def setBefore(self, before):
        self.before = before

def getKs(code, type):
    if(len(code) == 6):
        scode = ("sse" if code[0] == '6' else "szse") + code
        urltime = time.strftime("%Y%m%d%H%M%S", time.localtime())
        url = "http://webstock.quote.hermes.hexun.com/a/kline?code="+scode+"&start="+urltime+"&number=-1000&type="+str(type)+"&callback=callback"
        print(url)
        ua_header = {"User-Agent" : "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0;"}
        req = ur.Request(url, headers = ua_header)
        res = ur.urlopen(req)
        html = str(res.read())
        html = html[html.find("[[")+1:html.find("]]")+2]
        json = _json.loads(html)
        ks = []
        for k in json:
            ks.append(K(str(k[0])[0:8], k[1]/100.0, k[2]/100.0))
        ks.reverse()
        i = 0;
        for k in ks:
            print(k.date+','+str(k.open))
            if (i < len(ks) - 1) :
                k.setBefore(ks[i+1])
            i += 1



if __name__ == '__main__':
    getKs("600600", 5)