/**
 * No 'Access-Control-Allow-Origin' header is present on the requested resource. Origin 'null' is therefore not allowed access.
 * C:\Users\fankai\AppData\Local\360Chrome\Chrome\Application\360chrome.exe --user-data-dir="C:/Chrome dev session" --disable-web-security
 */
var test = false;
function log(o){
	console.log(o);
}
function error(o){
	console.error(o);
}
function print(o){
	$('body').append(o)
}
function println(o){
	$('body').append('<br>'+new Date().format("[hh:mm:ss] ")+o)
}
Object.prototype.to$ = function(){
	return $(this)
}
function fomatFloat(src,pos){   
    return Math.round(src*Math.pow(10, pos))/Math.pow(10, pos);   
}
Date.prototype.format = function (fmt) { //author: meizz 
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "h+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}

function wait(sec, func){
	var flag = false;
	var interval = window.setInterval(tempFunc, sec);
	function tempFunc(){
		flag = func.call()
		if(flag == true){
			window.clearInterval(interval)
		}
	}
}

function then(sec, flag, func){
	flag = false;
	var interval = window.setInterval(tempFunc, sec);
	function tempFunc(){
		flag = func.call()
		if(flag == true){
			window.clearInterval(interval)
		}
	}
}

function then2(sec, flag){
	for(var i=2; i<arguments.length; i++){
		var func = arguments[i]
		if(flag) then(sec,flag, func)
	}
}

jQuery.fn.extend({
	findByCode: function(code){
		var result = $.grep(this, function(share){
			return share.code == code
		})
		return result.length==1?result[0]:null
	},
	removeByCode: function(code){
		for(var i=0; i<this.length; i++){
			if(this[i].code == code){
				this.splice(i,1)
				return true;
			}
		}
	}
});

function getLocation(code){
	if(typeof code === 'number'){
		code = new String(code)
	}
	if(isNaN(code)) return ''
	if(code.charAt(0) == '6'){
		return 'sh'
	}else{
		return 'sz'
	}
}

function getKsFromSina(shares, callback) {
	if(shares.length == 0)return
	var scode = shares.map(function(i, share){
		return share.getLocationCode()
	});
	//log(scode.toArray().join(','))
	
	var result = null;
	$.ajax({
	  type: 'get',
	  async:false,
	  //dataType:'script',
	  //cache:true,
	  url: 'http://hq.sinajs.cn/list='+scode.toArray().join(','),
	  success: function (data) {
		  result = data
		  if(callback != undefined)callback(data, shares);
	  },
	  error:function(xhr, textStatus, thrownError){
	      alert(thrownError);
	  }
	});
	return result;
}

var sina = false;

function updateSinaData(data, shares){
	eval(data)
	//shares.each(function(i, share){
	for(var i=0; i<shares.length; i++){
		share = shares[i]
		//eval("hq_str = hq_str_"+share.getLocationCode())
		var hq_str = eval('hq_str_'+share.getLocationCode())
		log(hq_str)
		var ss = hq_str.split(',')
		share.name = ss[0];
		var d = moment(ss[30]+' '+ss[31])
		var latestk = share.getK()
		var a = parseFloat(ss[9])
		var v = parseInt(ss[8])
		if(latestk.id == null){
			a -= latestk.a
			v -= latestk.v
			if(a == 0) continue;
		}else{
			a = 0
			v = 0
		}
		var k = new K({close:parseFloat(ss[3]), volume:v, v:parseInt(ss[8]), amount:a, a:parseFloat(ss[9]), time: d.toDate()})
		share.addK(k)
		log(share)
		log('Update ['+share.code+'] from Sinajs Finished.')
	}
	sina = true;
}
function Key2Value(key, value){
	this.key = key;
	this.value = value;
}

Key2Value.prototype.inRange = function(data){ // 区间：(, ]
	if(this.key != null && data <= this.key){
		return false
	}
	if(this.value != null && data > this.value){
		return false
	}
	return true
}

//--------------
/** Strategy **/
//--------------
var strategy = {name:'突破平台', 
	condition:{
		atime:[60,120,180,240/*,300*/],//second 不计分
		amount:[/*new Key2Value(1.2, 1.5),*/ new Key2Value(1.5, 2.5), new Key2Value(2.5, 4), new Key2Value(4, null)],
		time:[2.5, 3, 4] //hour
	}
};
strategy.prototype = {
	run: function(self){
		//self = this
		k = self.getK();
		var atime = strategy.condition.atime
		
		for(var i=0; i<atime.length; i++){
			at = atime[i]
			var asum = k.getASum(at)
			//log(at+',asum=='+asum)
			var kb = k.getK(at).before
			var amax, amaxk, rk, asummax;

			for(var j=0; j<strategy.condition.time.length; j++){
				t = strategy.condition.time[j]
				var cmaxk = kb.getKCH(t*3600)
				if(k.close < cmaxk.close){
					break;
				}
				var k2 = k.getK(at*5).before
				//log(cmaxk.time+'-------------'+k2.time)
				if(cmaxk.time.getTime() >= k2.time.getTime()){
					continue;
				}
				amaxk = kb.getKAMax(t*3600, at)
				amax = amaxk.getASum(at)
				asummax = asum/amax
				rk = range(asummax, strategy.condition.amount)
				//log(at+','+t+','+asummax+","+rk)
				if(rk >= 0){
					log('amax='+amax+",asum="+asum+',amaxk====================================='+amaxk.time)
					var rank = {name:strategy.name}
					rank.atime = [at, 0]
					rank.amount = [asummax, strategy.condition.amount[rk].key, rk+1]
					rank.time = [t, j+1]
					rank.rank = rk+1+j+1
					log(rank)
					shares.removeByCode(self.code)
					println('['+k.time.format('hh:mm:ss')+']'+self.name+'['+self.code+']'+rank.name+',评级:'+rank.rank+' - 突破'+rank.time[0]+'小时新高，成交额在'+(rank.atime[0]/60)+'分钟内放大'+fomatFloat(rank.amount[0],2)+'倍')
					return rank
				}
			}
			
		}
		return null;
	}
}

function range(data, arr){
	for(var i=0; i<arr.length; i++){
		var kv = arr[i]
		if(kv.inRange(data)){
			return i
		}
	}
	return -1;
}

//global variable
var initinal = 0;
/**
 * Class
 */
function Share(code, name){
	this.code = code;
	this.name = name;
	this.ks = new Array();
}
Share.prototype = {
	getLocation: function(){
		return getLocation(this.code);
	},
	getLocationCode: function(){
		return getLocation(this.code)+this.code;
	},
	getK: function(n){
		if(n == undefined){
			return this.ks[this.ks.length-1];
		}
		return this.ks[n];
	},
	
	addK: function(k){
		var date = k.time;
		var hour = date.getHours()
		var minute = date.getMinutes()
		if(hour >= 15 /*&& minute > 0*/) return false
		
		var latestK = this.getK();
		if(latestK != undefined && latestK.time.getTime() >= k.time.getTime())return false
		/*if(k.id != null && k.id <= latestK.id){
			return false;
		}*/
		k.before = latestK
		if(k.change == undefined && k.before != undefined){
			k.change = k.close-k.before.close
		}
		this.ks.push(k);
		
		return true
	},
	
	initK: function(today){
		//http://web.ifzq.gtimg.cn/appstock/app/day/query?_var=fdays_data_sh600600&code=sh600600
		var scode = this.getLocationCode();
		var result = null
		$.ajax({
		  type: 'get',
		  async:false,
		  //dataType:'script',
		  //cache:true,
		  url: 'http://web.ifzq.gtimg.cn/appstock/app/day/query?_var=fdays_data_'+scode+'&code='+scode,
		  success: function (data) {
			  result = data
		  },
		  error:function(xhr, textStatus, thrownError){
		      alert(thrownError);
		  }
		});
		//log(result)
		eval(result)
		var fd = eval('fdays_data_'+scode);
		var data = fd.data[scode].data;
		
		var self = this
		data.reverse().to$().each(function(i, dd){
			if(dd.date != today.replace(/-/g,'')){
				var date = dd.date;
				var dt = dd.data.to$()
				var tmpv = null
				var tmpa = null
				dt.each(function(i, d){
					var s = d.split(' ')
					var v = parseInt(s[2])
					var a = parseInt(s[3])
					if(tmpv != null){
						v -= tmpv
						a -= tmpa
					}
					tmpv = parseInt(s[2])
					tmpa = parseInt(s[3])
					var c = parseFloat(s[1])
					var m = moment(date.replace(/^(\d{4})(\d{2})(\d{2})$/, "$1-$2-$3")+' '+s[0].replace(/^(\d{2})(\d{2})$/, "$1:$2:00"))
					var k = new K({close:c, volume:v, amount:a, time:m.toDate()})
					self.addK(k)
				})
			}
		})
		this._getPrice(0, today)
	},
	
	getValueFromK: function(sec, func){
		var k = this.getK();
		return k.getValue(sec, func);
	},
	getKHighestBefore: function(sec){
		return this.getValueFromK(sec, function(result){
			if(result == null || this.close >= result.close){
				return this
			}
			return result;
		})
	},
	
	runStrategy: function(){
		self = this
		k = self.getK();
		var atime = strategy.condition.atime
		
		for(var i=0; i<atime.length; i++){
			at = atime[i]
			var asum = k.getASum(at)
			//log(at+',asum=='+asum)
			var kb = k.getK(at).before
			var amax, amaxk, rk, asummax;

			for(var j=0; j<strategy.condition.time.length; j++){
				t = strategy.condition.time[j]
				var cmaxk = kb.getKCH(t*3600)
				if(k.close < cmaxk.close){
					break;
				}
				var k2 = k.getK(at*5).before
				//log(cmaxk.time+'-------------'+k2.time)
				if(cmaxk.time.getTime() >= k2.time.getTime()){
					continue;
				}
				amaxk = kb.getKAMax(t*3600, at)
				amax = amaxk.getASum(at)
				asummax = asum/amax
				rk = range(asummax, strategy.condition.amount)
				//log(at+','+t+','+asummax+","+rk)
				if(rk >= 0){
					log('amax='+amax+",asum="+asum+',amaxk====================================='+amaxk.time)
					var rank = {name:strategy.name}
					rank.atime = [at, 0]
					rank.amount = [asummax, strategy.condition.amount[rk].key, rk+1]
					rank.time = [t, j+1]
					rank.rank = rk+1+j+1
					log(rank)
					shares.removeByCode(self.code)
					println('['+k.time.format('hh:mm:ss')+']'+self.name+'['+self.code+']'+rank.name+',评级:'+rank.rank+' - 突破'+rank.time[0]+'小时新高，成交额在'+(rank.atime[0]/60)+'分钟内放大'+fomatFloat(rank.amount[0],2)+'倍')
					return rank
				}
			}
			
		}
		return null;
	},

	
	_parser: function(data, today){
		if(data != null){
			var ks = data[1]
			var ss = ks.split('|');
			for(var i=0; i<ss.length; i++){
				var s = ss[i].split('/');
				var m = moment(today+' '+s[1])
				this.addK(new K({id:s[0], time:m.toDate(), close:parseFloat(s[2]), change:s[3], volume:parseInt(s[4]), amount:parseInt(s[5]), flag:s[6]}))
				
				testRank(this, today+' '+s[1])
			}
			return true;
		}else{
			return false;
		}
	},
	_getPrice: function(page, today){
		var scode = this.getLocationCode();
		var self = this;
		$.ajax({
		  type: 'get',
		  dataType: 'script',
		  //async:false,
		  url: 'http://stock.gtimg.cn/data/index.php?appn=detail&action=data&c='+scode+'&p='+page,
		  success: function (data) {
		      //log(data);
			  var v_detail_data = eval("v_detail_data_"+scode)
			  //log(page+","+self.ks.length)
			  if(self._parser(v_detail_data, today)){
				  self._getPrice(++page, today)
			  }else{
				  initinal ++
				  //log(initinal)
			  }
		  },
		  beforeSend: function(xhr){
			  eval("v_detail_data_"+scode+"=null;")
		  },
		  dataFilter: function(data, type){
			  //document.write(data);
		  },
		  error:function(xhr, textStatus, thrownError){
			  alert(xhr.statusText);
		      alert(xhr.responseText);
		      alert(xhr.status);
		      alert(textStatus);
		      alert(thrownError);
		  }
		});
	}
}

function K(obj){
	this.before = null;
	this.id = null;
	if(arguments.length > 0){
		/*for(key in obj){ 
	        this[key] = obj[key];
		}*/
		jQuery.extend(true, this, obj)
	}
}
K.prototype = {
	contrustor: K,
	getBefore: function(n){
		var result = this.before;
		while(n != undefined && n-- >= 2 && result != null){
			result = result.before;
		}
		return result;
	},
	getValue: function(sec, func){
		var k = this;
		var result = null;
		while(true){
			if(func != undefined){
				result = func.call(k, result)
			}
			var t = k.time.getTime()/1000
			var tb = k.before.time.getTime()/1000
			var diff = t - tb;
			if(diff > 600){
				diff = 60 - k.before.time.getSeconds() + k.time.getSeconds()
			}
			sec -= diff;
			
			if(sec == 0){
				if(func != undefined){
					return result
				}
				return k.before
			}else if(sec < 0){
				if(func != undefined){
					return result
				}
				return k;
			}
			k = k.before;
			if(k == null)break;
		}
	},
	getK: function(sec){
		return this.getValue(sec)
	},
	getKCH: function(sec){
		return this.getValue(sec, function(result){
			if(result == null || this.close >= result.close){
				return this
			}
			return result;
		})
	},
	getVSum: function(sec){
		return this.getValue(sec, function(result){
			if(result == null)return result = this.volume
			return result += this.volume
		})
	},
	getASum: function(sec){
		return this.getValue(sec, function(result){
			if(result == null)return result = this.amount
			return result += this.amount
		})
	},
	getKAMax: function(sec, step){//get end k
		return this.getValue(sec, function(result){
			if(result == null)return result = this
			var sum = this.getASum(step);
			var sum1 = result.getASum(step)
			if(sum > sum1){
				return this
			}
			return result
		})
	},
	getAMax: function(sec, step){
		return this.getKAMax(sec, step).getASum(step)
	}
}

var sh = new Share('sh000001');
var sh000001 = getKsFromSina([sh].to$())
eval(sh000001)
var shk = eval('hq_str_sh000001')
var today = shk.split(',')[30]
print('Today: '+today)


var shares = new Array();
//var codes = ['000157','000401','000425','000625','002020','002048','002106','002136','002167','002182','002221','002273','002310','002340','002446','002536','002635','002699','002727','002763','002780','300040','300068','300086','300088','300109','300127','300146','300274','300316','300347','300364','300409','600031','600312','600337','600380','600606','601038','601166','601198','601318','601633','603899','603939'];
var codes = ['300274'];
println('Monitoring Shares: '+codes)
$(codes).each(function(i, code){
	log('code=='+code)
	var s = new Share(code)
	s.initK(today)
	shares.push(s);
})
shares = shares.to$()

wait(1000, function(){
	if(initinal >= codes.length){
		println('Get History K Finished.')
		try{
			getKsFromSina(shares, updateSinaData)
		}catch(e){
			error(e);
			return true
		}
		return true
	}
	return false
});

wait(1000,function(){
	if(sina){
		main(shares)
		return true;
	}
	return false
});

var mainStop;
$(document).ready(function() {
	$("#btnStop").toggle(
			function(){
				window.clearInterval(mainStop)
				$("#btnStop").val("GO")
			},
			function(){
				$("#btnStop").val("STOP")
				main(shares)
			}
	);
});

function main(shares){
	mainStop = window.setInterval(function(){
		shares.each(function(i, share){
			//----------------
			maintest(share)
			//----------------
			var rank = share.runStrategy();
			/*if(rank != null){
				println(share.name+'['+share.code+']'+rank.name+',评级:'+rank.rank+' - 突破'+rank.time[0]+'小时新高，成交额在'+(rank.atime[0]/60)+'分钟内放大'+fomatFloat(rank.amount[0],2)+'倍')
			}*/
		});
		getKsFromSina(shares, updateSinaData)
	}, 5000);
}

function testRank(share, time){
	if(!test)return
	if(shares.findByCode(share.code) == null)return
	strategy[0].run(share)
	/*if(rank != null){
		log(share)
		println("["+time+"]:"+share.name+'['+share.code+']'+rank.name+',评级:'+rank.rank+' - 突破'+rank.time[0]+'小时新高，成交额在'+(rank.atime[0]/60)+'分钟内放大'+fomatFloat(rank.amount[0],2)+'倍')
	}*/
}

function maintest(share){
	if(!test)return
	k = share.getK();
	k = k.getKCH(60 * 60 * 2 + 60*30)
	log(k)
	k = share.getK();
	k = k.getValue(60 * 60 * 2 + 60*30)
	log(k)
	
	log('kkkkkkkkkkkkkkkkkk='+k.id)
	k = share.getK();
	k = k.getVSum(60*5)
	log('sum='+k)
	
	log('-----')
	k = share.getK();
	log(k.getKAMax(60 * 60 * 2 + 60*30, 60))
	log(k.getAMax(60 * 60 * 2 + 60*30, 60))
	
	share.addK(new K({amount:100000, close:15.5, time:new Date('2017-07-23 09:30:10')}))
	share.addK(new K({amount:100000, close:15.6, time:new Date('2017-07-23 09:30:20')}))
	share.addK(new K({amount:100000, close:15.5, time:new Date('2017-07-23 09:31:20')}))
	share.addK(new K({amount:200000, close:15.6, time:new Date('2017-07-23 09:33:20')}))
	share.addK(new K({amount:1300000, close:15.6, time:new Date('2017-07-23 09:35:00')}))
}