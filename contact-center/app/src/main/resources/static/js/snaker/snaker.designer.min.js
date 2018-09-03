(function(snaker){
	var designer={};
	designer.config={
		editable:true,
		lineHeight:15,
		basePath:"",
		rect:{
			attr:{
				x:10,y:10,width:100,height:50,r:5,fill:"90-#fff-#C0C0C0",stroke:"#000","stroke-width":1
			},
			showType:"image&text",
			type:"state",
			name:{text:"state","font-style":"italic"},
			text:{text:"状态","font-size":13},
			margin:5,
			props:[],
			img:{}
		},
		path:{
			attr:{
				path:{path:"M10 10L100 100",stroke:"#808080",fill:"none","stroke-width":2},
				arrow:{path:"M10 10L10 10",stroke:"#808080",fill:"#808080","stroke-width":2,radius:4},
				fromDot:{width:5,height:5,stroke:"#fff",fill:"#000",cursor:"move","stroke-width":2},
				toDot:{width:5,height:5,stroke:"#fff",fill:"#000",cursor:"move","stroke-width":2},
				bigDot:{width:5,height:5,stroke:"#fff",fill:"#000",cursor:"move","stroke-width":2},
				smallDot:{width:5,height:5,stroke:"#fff",fill:"#000",cursor:"move","stroke-width":3}
			},
			text:{text:"",cursor:"move",background:"#000"},
			textPos:{x:0,y:-10},
			props:{
				name:{name:"name",label:"名称",value:"",editor:function(){return new designer.editors.inputEditor()}},
				displayName:{name:"displayName",label:"显示",value:"",editor:function(){return new designer.editors.textEditor()}},
                expr:{name:"expr",label:"表达式",value:"",editor:function(){return new designer.editors.inputEditor()}}
			}
		},
		tools:{
			attr:{
				left:10,top:10
			},
			pointer:{},
			path:{},
			states:{},
			save:{
				onclick:function(c){alert(c)}
			}
		},
		props:{
			attr:{top:328,left:10},
			props:{}
		},
		restore:"",
		activeRects:{
			rects:[],
			rectAttr:{stroke:"#ff0000","stroke-width":2}
		},
		historyRects:{
			rects:[],
			pathAttr:{
				path:{stroke:"#00ff00"},
				arrow:{stroke:"#00ff00",fill:"#00ff00"}
			}
		}
	};
	designer.util={
		isLine:function(g,f,e){
			var d,c;
			if((g.x-e.x)==0){
				d=1
			}else{
				d=(g.y-e.y)/(g.x-e.x)
			}
			c=(f.x-e.x)*d+e.y;
			if((f.y-c)<10&&(f.y-c)>-10){
				f.y=c;
				return true
			}
			return false
		},
		center:function(d,c){
			return{x:(d.x-c.x)/2+c.x,y:(d.y-c.y)/2+c.y}
		},
		nextId:(function(){
			var c=0;
			return function(){return ++c}
		})(),
		connPoint:function(j,d){
			var c=d,e={x:j.x+j.width/2,y:j.y+j.height/2};
			var l=(e.y-c.y)/(e.x-c.x);
			l=isNaN(l)?0:l;
			var k=j.height/j.width;
			var h=c.y<e.y?-1:1,f=c.x<e.x?-1:1,g,i;
			if(Math.abs(l)>k&&h==-1){
				g=e.y-j.height/2;i=e.x+h*j.height/2/l
			}else{
				if(Math.abs(l)>k&&h==1){
					g=e.y+j.height/2;i=e.x+h*j.height/2/l
				}else{
					if(Math.abs(l)<k&&f==-1){
						g=e.y+f*j.width/2*l;
						i=e.x-j.width/2
					}else{
						if(Math.abs(l)<k&&f==1){
							g=e.y+j.width/2*l;i=e.x+j.width/2
						}
					}
				}
			}
			return{x:i,y:g}
		},
		arrow:function(l,k,d){
			var g=Math.atan2(l.y-k.y,k.x-l.x)*(180/Math.PI);
			var h=k.x-d*Math.cos(g*(Math.PI/180));
			var f=k.y+d*Math.sin(g*(Math.PI/180));
			var e=h+d*Math.cos((g+120)*(Math.PI/180));
			var j=f-d*Math.sin((g+120)*(Math.PI/180));
			var c=h+d*Math.cos((g+240)*(Math.PI/180));
			var i=f-d*Math.sin((g+240)*(Math.PI/180));
			return[k,{x:e,y:j},{x:c,y:i}]
		}
	};
	designer.rect=function(p,m){
		var u=this,g="rect"+designer.util.nextId(),E=snaker.extend(true,{},designer.config.rect,p),C=m,t,e,n,f,x,v;
		t=C.rect(E.attr.x,E.attr.y,E.attr.width,E.attr.height,E.attr.r).hide().attr(E.attr);
		e=C.image(designer.config.basePath+E.img.src,E.attr.x+E.img.width/2,E.attr.y+(E.attr.height-E.img.height)/2,E.img.width,E.img.height).hide();
		n=C.text(E.attr.x+E.img.width+(E.attr.width-E.img.width)/2,E.attr.y+designer.config.lineHeight/2,E.name.text).hide().attr(E.name);
		f=C.text(E.attr.x+E.img.width+(E.attr.width-E.img.width)/2,E.attr.y+(E.attr.height-designer.config.lineHeight)/2+designer.config.lineHeight,E.text.text).hide().attr(E.text);
		t.drag(function(r,o){A(r,o)},function(){z()},function(){l()});
		e.drag(function(r,o){A(r,o)},function(){z()},function(){l()});
		n.drag(function(r,o){A(r,o)},function(){z()},function(){l()});
		f.drag(function(r,o){A(r,o)},function(){z()},function(){l()});
		var A=function(F,r){
			if(!designer.config.editable){
				return
			}
			var o=(x+F);
			var G=(v+r);
			q.x=o-E.margin;
			q.y=G-E.margin;
			B()
		};
		var z=function(){
			x=t.attr("x");
			v=t.attr("y");
			t.attr({opacity:0.5});
			e.attr({opacity:0.5});
			f.attr({opacity:0.5})
		};
		var l=function(){
			t.attr({opacity:1});
			e.attr({opacity:1});
			f.attr({opacity:1})
		};
		var s,i={},h=5,q={x:E.attr.x-E.margin,y:E.attr.y-E.margin,width:E.attr.width+E.margin*2,height:E.attr.height+E.margin*2};
		s=C.path("M0 0L1 1").hide();
		i.t=C.rect(0,0,h,h).attr({fill:"#000",stroke:"#fff",cursor:"s-resize"}).hide().drag(function(r,o){D(r,o,"t")},function(){k(this.attr("x")+h/2,this.attr("y")+h/2,"t")},function(){});
		i.lt=C.rect(0,0,h,h).attr({fill:"#000",stroke:"#fff",cursor:"nw-resize"}).hide().drag(function(r,o){D(r,o,"lt")},function(){k(this.attr("x")+h/2,this.attr("y")+h/2,"lt")},function(){});
		i.l=C.rect(0,0,h,h).attr({fill:"#000",stroke:"#fff",cursor:"w-resize"}).hide().drag(function(r,o){D(r,o,"l")},function(){k(this.attr("x")+h/2,this.attr("y")+h/2,"l")},function(){});
		i.lb=C.rect(0,0,h,h).attr({fill:"#000",stroke:"#fff",cursor:"sw-resize"}).hide().drag(function(r,o){D(r,o,"lb")},function(){k(this.attr("x")+h/2,this.attr("y")+h/2,"lb")},function(){});
		i.b=C.rect(0,0,h,h).attr({fill:"#000",stroke:"#fff",cursor:"s-resize"}).hide().drag(function(r,o){D(r,o,"b")},function(){k(this.attr("x")+h/2,this.attr("y")+h/2,"b")},function(){});
		i.rb=C.rect(0,0,h,h).attr({fill:"#000",stroke:"#fff",cursor:"se-resize"}).hide().drag(function(r,o){D(r,o,"rb")},function(){k(this.attr("x")+h/2,this.attr("y")+h/2,"rb")},function(){});
		i.r=C.rect(0,0,h,h).attr({fill:"#000",stroke:"#fff",cursor:"w-resize"}).hide().drag(function(r,o){D(r,o,"r")},function(){k(this.attr("x")+h/2,this.attr("y")+h/2,"r")},function(){});
		i.rt=C.rect(0,0,h,h).attr({fill:"#000",stroke:"#fff",cursor:"ne-resize"}).hide().drag(function(r,o){D(r,o,"rt")},function(){k(this.attr("x")+h/2,this.attr("y")+h/2,"rt")},function(){});
		var D=function(F,r,G){
			if(!designer.config.editable){return}
			var o=_bx+F,H=_by+r;
			switch(G){
				case"t":
					q.height+=q.y-H;
					q.y=H;
					break;
				case"lt":
					q.width+=q.x-o;
					q.height+=q.y-H;
					q.x=o;
					q.y=H;
					break;
				case"l":q.width+=q.x-o;q.x=o;break;
				case"lb":q.height=H-q.y;q.width+=q.x-o;q.x=o;break;
				case"b":q.height=H-q.y;break;
				case"rb":q.height=H-q.y;q.width=o-q.x;break;
				case"r":q.width=o-q.x;break;
				case"rt":q.width=o-q.x;q.height+=q.y-H;q.y=H;break
			}
			B()
		};
		var k=function(r,o,F){
			_bx=r;
			_by=o
		};
		snaker([t.node,f.node,n.node,e.node]).bind("click",function(){
			if(!designer.config.editable){return}w();
			var o=snaker(C).data("mod");
			switch(o){
				case"pointer":break;
				case"path":var r=snaker(C).data("currNode");
				if(r&&r.getId()!=g&&r.getId().substring(0,4)=="rect"){
					snaker(C).trigger("addpath",[r,u])
				}
				break
			}
			snaker(C).trigger("click",u);
			snaker(C).data("currNode",u);
			return false
		});
		var j=function(o,r){
			if(!designer.config.editable){
				return
			}
			if(r.getId()==g){
				snaker(C).trigger("showprops",[E.props,r])
			}else{
				d()
			}
		};
		snaker(C).bind("click",j);
		var c=function(o,F,r){
			if(r.getId()==g){
				f.attr({text:F})
			}
		};
		snaker(C).bind("textchange",c);
		function y(){
			return"M"+q.x+" "+q.y+"L"+q.x+" "+(q.y+q.height)+"L"+(q.x+q.width)+" "+(q.y+q.height)+"L"+(q.x+q.width)+" "+q.y+"L"+q.x+" "+q.y
		}
		function w(){s.show();for(var o in i){i[o].show()}}
		function d(){s.hide();for(var o in i){i[o].hide()}}
		function B(){
			var F=q.x+E.margin,r=q.y+E.margin,G=q.width-E.margin*2,o=q.height-E.margin*2;
			t.attr({x:F,y:r,width:G,height:o});
			switch(E.showType){
				case"image":e.attr({x:F+(G-E.img.width)/2,y:r+(o-E.img.height)/2}).show();break;
				case"text":t.show();f.attr({x:F+G/2,y:r+o/2}).show();break;
				case"image&text":
					t.show();
					n.attr({x:F+E.img.width+(G-E.img.width)/2,y:r+designer.config.lineHeight/2}).show();
					f.attr({x:F+E.img.width+(G-E.img.width)/2,y:r+(o-designer.config.lineHeight)/2+designer.config.lineHeight}).show();
					e.attr({x:F+E.img.width/2,y:r+(o-E.img.height)/2}).show();break
			}
			i.t.attr({x:q.x+q.width/2-h/2,y:q.y-h/2});
			i.lt.attr({x:q.x-h/2,y:q.y-h/2});
			i.l.attr({x:q.x-h/2,y:q.y-h/2+q.height/2});
			i.lb.attr({x:q.x-h/2,y:q.y-h/2+q.height});
			i.b.attr({x:q.x-h/2+q.width/2,y:q.y-h/2+q.height});
			i.rb.attr({x:q.x-h/2+q.width,y:q.y-h/2+q.height});
			i.r.attr({x:q.x-h/2+q.width,y:q.y-h/2+q.height/2});
			i.rt.attr({x:q.x-h/2+q.width,y:q.y-h/2});
			s.attr({path:y()});
			snaker(C).trigger("rectresize",u)
		}
		this.toJson=function(){
			var r="{type:'"+E.type+"',text:{text:'"+f.attr("text")+"'}, attr:{ x:"+Math.round(t.attr("x"))+", y:"+Math.round(t.attr("y"))+", width:"+Math.round(t.attr("width"))+", height:"+Math.round(t.attr("height"))+"}, props:{";
			for(var o in E.props){
				r+=o+":{value:'"+E.props[o].value+"'},"
			}
			if(r.substring(r.length-1,r.length)==","){
				r=r.substring(0,r.length-1)
			}
			r+="}}";
			return r
		};
		this.toBeforeXml=function(){
			var r="<"+E.type+" layout=\""+(Math.round(t.attr("x"))-180)+","+Math.round(t.attr("y"))+","+Math.round(t.attr("width"))+","+Math.round(t.attr("height"))+"\" ";
			for(var o in E.props){
				if(o=="name"&&E.props[o].value==""){
					alert(E.type+" 名称 不能为空");
					return ""
				}
				if(o=="layout") continue;
				if(E.props[o].value!=""){
					var vv = E.props[o].value.replace(/>/g,"#5");
					vv = vv.replace(/</g,"#6");
                    vv = vv.replace(/&/g,"#7");
					r+=o+"=\""+vv+"\" "
				}
			}
			r+=">";
			return r
		};
		this.toAfterXml=function(){
			var r="</"+E.type+">";
			return r
		};
		this.restore=function(o){
			var r=o;
			E=snaker.extend(true,E,o);
			f.attr({text:r.text.text});
			B()
		};
		this.getName=function(){
			for(var o in E.props){
				if(o=="name"){
					return E.props[o].value;
				}
			}
		}
		this.getBBox=function(){return q};
		this.getId=function(){return g};
		this.remove=function(){
			t.remove();
			f.remove();
			n.remove();
			e.remove();
			s.remove();
			for(var o in i){
				i[o].remove()
			}
		};
		this.text=function(){return f.attr("text")};
		this.attr=function(o){
			if(o){t.attr(o)}
		};
		B()
	};
	designer.path=function(q,n,u,e){
		var v=this,z=n,B=snaker.extend(true,{},designer.config.path),i,t,f,h=B.textPos,y,w,k=u,s=e,g="path"+designer.util.nextId(),x;
		function p(G,H,Left,L){
			var F=this,M=G,r,o=Left,O=L,K,I,N=H;
			switch(M){
				case"from":r=z.rect(H.x-B.attr.fromDot.width/2,H.y-B.attr.fromDot.height/2,B.attr.fromDot.width,B.attr.fromDot.height).attr(B.attr.fromDot);break;
				case"big":r=z.rect(H.x-B.attr.bigDot.width/2,H.y-B.attr.bigDot.height/2,B.attr.bigDot.width,B.attr.bigDot.height).attr(B.attr.bigDot);break;
				case"small":r=z.rect(H.x-B.attr.smallDot.width/2,H.y-B.attr.smallDot.height/2,B.attr.smallDot.width,B.attr.smallDot.height).attr(B.attr.smallDot);break;
				case"to":r=z.rect(H.x-B.attr.toDot.width/2,H.y-B.attr.toDot.height/2,B.attr.toDot.width,B.attr.toDot.height).attr(B.attr.toDot);break
			}
			if(r&&(M=="big"||M=="small")){
				r.drag(function(Q,P){C(Q,P)},function(){J()},function(){E()});
				var C=function(R,Q){var P=(K+R),S=(I+Q);F.moveTo(P,S)};
				var J=function(){
					if(M=="big"){K=r.attr("x")+B.attr.bigDot.width/2;I=r.attr("y")+B.attr.bigDot.height/2}
					if(M=="small"){K=r.attr("x")+B.attr.smallDot.width/2;I=r.attr("y")+B.attr.smallDot.height/2}
				};
				var E=function(){}
			}
			this.type=function(P){if(P){M=P}else{return M}};
			this.node=function(P){if(P){r=P}else{return r}};
			this.left=function(P){if(P){o=P}else{return o}};
			this.right=function(P){if(P){O=P}else{return O}};
			this.remove=function(){o=null;O=null;r.remove()};
			this.pos=function(P){if(P){N=P;r.attr({x:N.x-r.attr("width")/2,y:N.y-r.attr("height")/2});return this}else{return N}};
			this.moveTo=function(Q,T){
				this.pos({x:Q,y:T});
				switch(M){
					case"from":if(O&&O.right()&&O.right().type()=="to"){O.right().pos(designer.util.connPoint(s.getBBox(),N))}if(O&&O.right()){O.pos(designer.util.center(N,O.right().pos()))}break;
					case"big":if(O&&O.right()&&O.right().type()=="to"){O.right().pos(designer.util.connPoint(s.getBBox(),N))}if(o&&o.left()&&o.left().type()=="from"){o.left().pos(designer.util.connPoint(k.getBBox(),N))}if(O&&O.right()){O.pos(designer.util.center(N,O.right().pos()))}if(o&&o.left()){o.pos(designer.util.center(N,o.left().pos()))}var S={x:N.x,y:N.y};if(designer.util.isLine(o.left().pos(),S,O.right().pos())){M="small";r.attr(B.attr.smallDot);this.pos(S);var P=o;o.left().right(o.right());o=o.left();P.remove();var R=O;O.right().left(O.left());O=O.right();R.remove()}break;
					case"small":if(o&&O&&!designer.util.isLine(o.pos(),{x:N.x,y:N.y},O.pos())){M="big";r.attr(B.attr.bigDot);var P=new p("small",designer.util.center(o.pos(),N),o,o.right());o.right(P);o=P;var R=new p("small",designer.util.center(O.pos(),N),O.left(),O);O.left(R);O=R}break;
					case"to":if(o&&o.left()&&o.left().type()=="from"){o.left().pos(designer.util.connPoint(k.getBBox(),N))}if(o&&o.left()){o.pos(designer.util.center(N,o.left().pos()))}break
				}m()
			}
		}
		function j(){
			var D,C,E=k.getBBox(),F=s.getBBox(),r,o;
			r=designer.util.connPoint(E,{x:F.x+F.width/2,y:F.y+F.height/2});
			o=designer.util.connPoint(F,r);
			D=new p("from",r,null,new p("small",{x:(r.x+o.x)/2,y:(r.y+o.y)/2}));
			D.right().left(D);
			C=new p("to",o,D.right(),null);
			D.right().right(C);
			this.toPathString=function(){
				if(!D){return""}
				var J=D,I="M"+J.pos().x+" "+J.pos().y,H="";
				while(J.right()){
					J=J.right();
					I+="L"+J.pos().x+" "+J.pos().y
				}
				var G=designer.util.arrow(J.left().pos(),J.pos(),B.attr.arrow.radius);
				H="M"+G[0].x+" "+G[0].y+"L"+G[1].x+" "+G[1].y+"L"+G[2].x+" "+G[2].y+"z";
				return[I,H]
			};
			this.toJson=function(){
				var G="[",H=D;
				while(H){
					if(H.type()=="big"){
						G+="{x:"+Math.round(H.pos().x)+",y:"+Math.round(H.pos().y)+"},"
					}
					H=H.right()
				}
				if(G.substring(G.length-1,G.length)==","){
					G=G.substring(0,G.length-1)
				}
				G+="]";
				return G
			};
			
			this.toXml=function(){
				var G="",H=D;
				while(H){
					if(H.type()=="big"){
						G+=(Math.round(H.pos().x)-180)+","+Math.round(H.pos().y)+";"
					}
					H=H.right()
				}
				if(G.substring(G.length-1,G.length)==";"){
					G=G.substring(0,G.length-1)
				}
				return G
			};
			this.restore=function(H){
				var I=H,J=D.right();
				for(var G=0;G<I.length;G++){
					J.moveTo(I[G].x,I[G].y);
					J.moveTo(I[G].x,I[G].y);
					J=J.right()
				}
				this.hide()
			};
			this.fromDot=function(){return D};
			this.toDot=function(){return C};
			this.midDot=function(){
				var H=D.right(),G=D.right().right();
				while(G.right()&&G.right().right()){
					G=G.right().right();
					H=H.right()
				}
				return H
			};
			this.show=function(){
				var G=D;
				while(G){
					G.node().show();
					G=G.right()
				}
			};
			this.hide=function(){
				var G=D;
				while(G){
					G.node().hide();
					G=G.right()
				}
			};
			this.remove=function(){
				var G=D;
				while(G){
					if(G.right()){
						G=G.right();
						G.left().remove()
					}else{
						G.remove();
						G=null
					}
				}
			}
		}
		B=snaker.extend(true,B,q);
		i=z.path(B.attr.path.path).attr(B.attr.path);
		t=z.path(B.attr.arrow.path).attr(B.attr.arrow);
		x=new j();
		x.hide();
		f=z.text(0,0,B.text.text).attr(B.text).attr({text:B.text.text.replace("{from}",k.text()).replace("{to}",s.text())});
		f.drag(function(r,o){
			if(!designer.config.editable){return}
			f.attr({x:y+r,y:w+o})
		},
		function(){y=f.attr("x");w=f.attr("y")},
		function(){
			var o=x.midDot().pos();
			h={x:f.attr("x")-o.x,y:f.attr("y")-o.y}
		});
		m();
		snaker([i.node,t.node]).bind("click",function(){
			if(!designer.config.editable){return}
			snaker(z).trigger("click",v);
			snaker(z).data("currNode",v);
			return false
		});
		var l=function(r,C){
			if(!designer.config.editable){return}
			if(C&&C.getId()==g){
				x.show();
				snaker(z).trigger("showprops",[B.props,v])
			}else{
				x.hide()
			}
			var o=snaker(z).data("mod");
			switch(o){
				case"pointer":break;
				case"path":break
			}
		};
		snaker(z).bind("click",l);
		var A=function(o,r){
			if(!designer.config.editable){return}
			if(r&&(r.getId()==k.getId()||r.getId()==s.getId())){
				snaker(z).trigger("removepath",v)
			}
		};
		snaker(z).bind("removerect",A);
		var d=function(C,D){
			if(!designer.config.editable){return}
			if(k&&k.getId()==D.getId()){
				var o;
				if(x.fromDot().right().right().type()=="to"){
					o={x:s.getBBox().x+s.getBBox().width/2,y:s.getBBox().y+s.getBBox().height/2}
				}else{
					o=x.fromDot().right().right().pos()
				}
				var r=designer.util.connPoint(k.getBBox(),o);
				x.fromDot().moveTo(r.x,r.y);
				m()
			}
			if(s&&s.getId()==D.getId()){
				var o;
				if(x.toDot().left().left().type()=="from"){
					o={x:k.getBBox().x+k.getBBox().width/2,y:k.getBBox().y+k.getBBox().height/2}
				}else{
					o=x.toDot().left().left().pos()
				}
				var r=designer.util.connPoint(s.getBBox(),o);
				x.toDot().moveTo(r.x,r.y);
				m()
			}
		};
		snaker(z).bind("rectresize",d);
		var c=function(r,o,C){
			if(C.getId()==g){
				f.attr({text:o})
			}
		};
		snaker(z).bind("textchange",c);
		this.from=function(){return k};
		this.to=function(){return s};
		this.toJson=function(){
			var r="{from:'"+k.getId()+"',to:'"+s.getId()+"', dots:"+x.toJson()+",text:{text:'"+f.attr("text")+"'},textPos:{x:"+Math.round(h.x)+",y:"+Math.round(h.y)+"}, props:{";
			for(var o in B.props){
				r+=o+":{value:'"+B.props[o].value+"'},"
			}
			if(r.substring(r.length-1,r.length)==","){
				r=r.substring(0,r.length-1)
			}
			r+="}}";
			return r
		};
		
		this.toXml=function(){
			var hx = Math.round(h.x);
			var r="<transition offset=\""+hx+","+Math.round(h.y)+"\" to=\""+s.getName()+"\" ";
			var dots=x.toXml();
			if(dots!="") r+=" g=\""+x.toXml()+"\" ";
			for(var o in B.props){
				if(o=="name"&&B.props[o].value==""){
					r+=o+"=\""+g+"\" ";
					continue;
				}
				if(B.props[o].value!=""){
					var vv = B.props[o].value.replace(/>/g,"#5");
					vv = vv.replace(/</g,"#6");
                    vv = vv.replace(/&/g,"#7");
					r+=o+"=\""+vv+"\" ";
				}
			}
			r+="/>";
			return r
		};
		this.restore=function(o){
			var r=o;
			B.props.displayName.value=r.text.text;
			B=snaker.extend(true,B,o);
			x.restore(r.dots)
		};
		this.remove=function(){
			x.remove();
			i.remove();
			t.remove();
			f.remove();
			try{
				snaker(z).unbind("click",l)
			}catch(o){}
			try{
				snaker(z).unbind("removerect",A)
			}catch(o){}
			try{
				snaker(z).unbind("rectresize",d)
			}catch(o){}
			try{
				snaker(z).unbind("textchange",c)
			}catch(o){}
		};
		function m(){
			var r=x.toPathString(),o=x.midDot().pos();
			i.attr({path:r[0]});
			t.attr({path:r[1]});
			f.attr({x:o.x+h.x,y:o.y+h.y})
		}
		this.getId=function(){return g};
		this.text=function(){
			return f.attr("text")
		};
		this.attr=function(o){
			if(o&&o.path){
				i.attr(o.path)
			}
			if(o&&o.arrow){
				t.attr(o.arrow)
			}
		}
	};
	designer.props=function(h,f){
		var j=this,c=snaker("#properties").hide().draggable({handle:"#properties_handle"}).resizable().css(designer.config.props.attr).bind("click",function(){return false}),e=c.find("table"),g=f,i;
		var d=function(n,m,o){
			if(i&&i.getId()==o.getId()){
				return
			}
			i=o;
			snaker(e).find(".editor").each(function(){
				var k=snaker(this).data("editor");
				if(k){k.destroy()}
			});
			e.empty();
			c.show();
			for(var l in m){
				if(!m[l].name) continue;
				if(m[l].name=="name"&&m[l].value==""){
					m[l].value=o.getId()
				}
				m[l].value=m[l].value.replace(/#1/g,"'");
				m[l].value=m[l].value.replace(/#2/g,"\"");
				m[l].value=m[l].value.replace(/#3/g,"\r\n");
				m[l].value=m[l].value.replace(/#4/g,"\n");
				m[l].value=m[l].value.replace(/#5/g,">");
				m[l].value=m[l].value.replace(/#6/g,"<");
                m[l].value=m[l].value.replace(/#7/g,"&");
				if(!m[l].label){
					continue;
				}
				e.append("<tr><td class='properties_name'>"+m[l].label+"</td><td class='properties_value'><div id='p"+l+"' class='editor'></div></td></tr>");
				if(m[l].editor){
					m[l].editor().init(m,l,"p"+l,o,g)
				}
			}
		};
		snaker(g).bind("showprops",d)
	};
	designer.editors={
		textEditor:function(){
			var d,e,c,g,f;
			this.init=function(i,h,m,l,j){
				d=i;e=h;c=m;g=l;f=j;
				snaker('<input style="width:98%;"/>').val(g.text()).change(function(){
					i[e].value=snaker(this).val();
					snaker(f).trigger("textchange",[snaker(this).val(),g])
				}).appendTo("#"+c);
				snaker("#"+c).data("editor",this)
			};
			this.destroy=function(){
				snaker("#"+c+" input").each(function(){
					d[e].value=snaker(this).val();
					snaker(f).trigger("textchange",[snaker(this).val(),g])
				})
			}
		}
	};
	designer.init=function(x,r){
		var v=snaker(window).width(),e=snaker(window).height(),y=Raphael(x,v*1.5,e*1.5),q={},g={};
		snaker.extend(true,designer.config,r);
		snaker(document).keydown(function(i){
			if(!designer.config.editable){return}
			if(i.keyCode==46){
				var j=snaker(y).data("currNode");
				if(j){
					if(j.getId().substring(0,4)=="rect"){
						snaker(y).trigger("removerect",j)
					}else{
						if(j.getId().substring(0,4)=="path"){
							snaker(y).trigger("removepath",j)
						}
					}
					snaker(y).removeData("currNode")
				}
			}
		});
		snaker(document).click(function(){
			snaker(y).data("currNode",null);
			snaker(y).trigger("click",{
				getId:function(){return ""}
			});
			snaker(y).trigger("showprops",[designer.config.props.props,{getId:function(){return ""}}])
		});
		var w=function(c,i){
			if(!designer.config.editable){return}
			if(i.getId().substring(0,4)=="rect"){
				q[i.getId()]=null;
				i.remove()
			}else{
				if(i.getId().substring(0,4)=="path"){
					g[i.getId()]=null;
					i.remove()
				}
			}
		};
		snaker(y).bind("removepath",w);
		snaker(y).bind("removerect",w);
		snaker(y).bind("addrect",function(j,c,k){
			var i=new designer.rect(snaker.extend(true,{},designer.config.tools.states[c],k),y);
			q[i.getId()]=i
		});
		var f=function(i,k,j){
			var c=new designer.path({},y,k,j);
			g[c.getId()]=c
		};
		snaker(y).bind("addpath",f);
		snaker(y).data("mod","point");
		if(designer.config.editable){
			snaker("#toolbox").draggable({handle:"#toolbox_handle"}).css(designer.config.tools.attr);
			snaker("#toolbox .node").hover(function(){snaker(this).addClass("mover")},function(){snaker(this).removeClass("mover")});
			snaker("#toolbox .selectable").click(function(){
				snaker(".selected").removeClass("selected");
				snaker(this).addClass("selected");
				snaker(y).data("mod",this.id)
			});
			snaker("#toolbox .state").each(function(){
				snaker(this).draggable({helper:"clone"})
			});
			snaker(x).droppable({
				accept:".state",
				drop:function(c,i){
					snaker(y).trigger("addrect",[i.helper.attr("type"),{attr:{x:i.helper.offset().left,y:i.helper.offset().top}}])
				}
			});
			snaker("#save").click(function(){
				/**
				var i="{states:{";
				for(var c in q){
					if(q[c]){
						i+=q[c].getId()+":"+q[c].toJson()+","
					}
				}
				if(i.substring(i.length-1,i.length)==","){
					i=i.substring(0,i.length-1)
				}
				i+="},paths:{";
				for(var c in g){
					if(g[c]){
						i+=g[c].getId()+":"+g[c].toJson()+","
					}
				}
				if(i.substring(i.length-1,i.length)==","){
					i=i.substring(0,i.length-1)
				}
				i+="},props:{props:{";
				for(var c in designer.config.props.props){
					i+=c+":{value:'"+designer.config.props.props[c].value+"'},"
				}
				if(i.substring(i.length-1,i.length)==","){
					i=i.substring(0,i.length-1)
				}
				i+="}}}";
				*/
				var i="<process ";
				for(var c in designer.config.props.props){
					if((c=="name"||c=="displayName")&&designer.config.props.props[c].value==""){
						alert("流程定义名称、显示名称不能为空");
						return;
					}
					if(designer.config.props.props[c].value!=""){
						i+=c+"=\""+designer.config.props.props[c].value+"\" "
					}
				}
				i+=">\n";
				for(var node in q){
					if(q[node]){
						i+=q[node].toBeforeXml();
						for(var transition in g){
							if(g[transition]){
								var from=g[transition].from().getId();
								if(from==q[node].getId()){
									var transitionXml=g[transition].toXml();
									if(transitionXml==""){
										alert("连接线名称不能为空");
										return
									}else{
										i+="\n";
										i+=transitionXml;
									}
								}
							}
						}
						i+="\n";
						i+=q[node].toAfterXml();
						i+="\n";
					}
				}
				i+="</process>";
				
				designer.config.tools.save.onclick(i)
			});
			new designer.props({},y)
		}
		if(r.restore){
			var B=r.restore;
			var z={};
			if(B.states){
				for(var s in B.states){
					var d=new designer.rect(snaker.extend(true,{},designer.config.tools.states[B.states[s].type],B.states[s]),y);
					d.restore(B.states[s]);
					z[s]=d;
					q[d.getId()]=d
				}
			}
			if(B.paths){
				for(var s in B.paths){
					var n=new designer.path(snaker.extend(true,{},designer.config.tools.path,B.paths[s]),y,z[B.paths[s].from],z[B.paths[s].to]);
					n.restore(B.paths[s]);
					g[n.getId()]=n
				}
			}
			if(B.props&&B.props.props){
				for(var s in designer.config.props.props){
					var tmp=designer.config.props.props[s];
					for(var ss in B.props.props){
						if(tmp.name==B.props.props[ss].name){
							tmp.value=B.props.props[ss].value;
							break;
						}
					}
				}
			}
		}
		var A=designer.config.historyRects,l=designer.config.activeRects;
		if(A.rects.length||l.rects.length){
			var m={},z={};
			for(var h in g){
				if(!z[g[h].from().text()]){
					z[g[h].from().text()]={rect:g[h].from(),paths:{}}
				}
				z[g[h].from().text()].paths[g[h].text()]=g[h];
				if(!z[g[h].to().text()]){
					z[g[h].to().text()]={rect:g[h].to(),paths:{}}
				}
			}
			for(var u=0;u<A.rects.length;u++){
				if(z[A.rects[u].name]){
					z[A.rects[u].name].rect.attr(A.rectAttr)
				}
				for(var t=0;t<A.rects[u].paths.length;t++){
					if(z[A.rects[u].name].paths[A.rects[u].paths[t]]){
						z[A.rects[u].name].paths[A.rects[u].paths[t]].attr(A.pathAttr)
					}
				}
			}
			for(var u=0;u<l.rects.length;u++){
				if(z[l.rects[u].name]){
					z[l.rects[u].name].rect.attr(l.rectAttr)
				}
				for(var t=0;t<l.rects[u].paths.length;t++){
					if(z[l.rects[u].name].paths[l.rects[u].paths[t]]){
						z[l.rects[u].name].paths[l.rects[u].paths[t]].attr(l.pathAttr)
					}
				}
			}
		}
	};
	snaker.fn.snakerflow=function(c){
		return this.each(function(){
			designer.init(this,c)
		})
	};
	snaker.snakerflow=designer})(jQuery);