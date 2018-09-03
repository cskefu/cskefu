(function(snaker) {

        var designer = {};

        designer.config = {
                editable : true,
                lineHeight : 15,
                basePath : '',
                rect : {// 状态
                        attr : {
                                x : 10,
                                y : 10,
                                width : 100,
                                height : 50,
                                r : 5,
                                fill : '90-#fff-#C0C0C0',
                                stroke : '#000',
                                "stroke-width" : 1
                        },
                        showType : 'image&text',// image,text,image&text
                        type : 'state',
                        name : {
                                text : 'state',
                                'font-style' : 'italic'
                        },
                        text : {
                                text : '状态',
                                'font-size' : 13
                        },
                        margin : 5,
                        props : [],
                        img : {}
                },
                path : {// 路径转换
                        attr : {
                                path : {
                                        path : 'M10 10L100 100',
                                        stroke : '#808080',
                                        fill : "none",
                                        "stroke-width" : 2
                                },
                                arrow : {
                                        path : 'M10 10L10 10',
                                        stroke : '#808080',
                                        fill : "#808080",
                                        "stroke-width" : 2,
                                        radius : 4
                                },
                                fromDot : {
                                        width : 5,
                                        height : 5,
                                        stroke : '#fff',
                                        fill : '#000',
                                        cursor : "move",
                                        "stroke-width" : 2
                                },
                                toDot : {
                                        width : 5,
                                        height : 5,
                                        stroke : '#fff',
                                        fill : '#000',
                                        cursor : "move",
                                        "stroke-width" : 2
                                },
                                bigDot : {
                                        width : 5,
                                        height : 5,
                                        stroke : '#fff',
                                        fill : '#000',
                                        cursor : "move",
                                        "stroke-width" : 2
                                },
                                smallDot : {
                                        width : 5,
                                        height : 5,
                                        stroke : '#fff',
                                        fill : '#000',
                                        cursor : "move",
                                        "stroke-width" : 3
                                }
                        },
						text : {
							text:"",
							cursor:"move",
							background:"#000"
						},
						textPos:{
							x:0,
							y:-10
						},
                        props : {
                                name : {
                                        name : 'name',
                                        label : '名称',
                                        value : '',
                                        editor : function() {
                                                return new designer.editors.inputEditor();
                                        }
                                },
								displayName : {
									    name:"displayName",
										label:"显示",
										value:"",
										editor:function(){
											return new designer.editors.textEditor();
										}
								}
                        }
                },
                tools : {// 工具栏
                        attr : {
                                right : 10,
                                top : 10
                        },
                        pointer : {},
                        path : {},
                        states : {},
                        save : {
                                onclick : function(data) {
                                        alert(data);
                                }
                        }
                },
                props : {// 属性编辑器
                        attr : {
                                top : 328,
                                left : 10
                        },
                        props : {}
                },
                restore : '',
                activeRects : {// 当前激活状态
                        rects : [],
                        rectAttr : {
                                stroke : '#ff0000',
                                "stroke-width" : 2
                        }
                },
                historyRects : {// 历史激活状态
                        rects : [],
                        pathAttr : {
                                path : {
                                        stroke : '#00ff00'
                                },
                                arrow : {
                                        stroke : '#00ff00',
                                        fill : "#00ff00"
                                }
                        }
                }
        };

        designer.util = {
                isLine : function(p1, p2, p3) {// 三个点是否在一条直线上
                        var s, p2y;
                        if ((p1.x - p3.x) == 0)
                                s = 1;
                        else
                                s = (p1.y - p3.y) / (p1.x - p3.x);
                        p2y = (p2.x - p3.x) * s + p3.y;
                        // $('body').append(p2.y+'-'+p2y+'='+(p2.y-p2y)+', ');
                        if ((p2.y - p2y) < 10 && (p2.y - p2y) > -10) {
                                p2.y = p2y;
                                return true;
                        }
                        return false;
                },
                center : function(p1, p2) {// 两个点的中间点
                        return {
                                x : (p1.x - p2.x) / 2 + p2.x,
                                y : (p1.y - p2.y) / 2 + p2.y
                        };
                },
                nextId : (function() {
                        var uid = 0;
                        return function() {
                                return ++uid;
                        };
                })(),

                connPoint : function(rect, p) {// 计算矩形中心到p的连线与矩形的交叉点
                        var start = p, end = {
                                x : rect.x + rect.width / 2,
                                y : rect.y + rect.height / 2
                        };
                        // 计算正切角度
                        var tag = (end.y - start.y) / (end.x - start.x);
                        tag = isNaN(tag) ? 0 : tag;

                        var rectTag = rect.height / rect.width;
                        // 计算箭头位置
                        var xFlag = start.y < end.y ? -1 : 1, yFlag = start.x < end.x
                                        ? -1
                                        : 1, arrowTop, arrowLeft;
                        // 按角度判断箭头位置
                        if (Math.abs(tag) > rectTag && xFlag == -1) {// top边
                                arrowTop = end.y - rect.height / 2;
                                arrowLeft = end.x + xFlag * rect.height / 2 / tag;
                        } else if (Math.abs(tag) > rectTag && xFlag == 1) {// bottom边
                                arrowTop = end.y + rect.height / 2;
                                arrowLeft = end.x + xFlag * rect.height / 2 / tag;
                        } else if (Math.abs(tag) < rectTag && yFlag == -1) {// left边
                                arrowTop = end.y + yFlag * rect.width / 2 * tag;
                                arrowLeft = end.x - rect.width / 2;
                        } else if (Math.abs(tag) < rectTag && yFlag == 1) {// right边
                                arrowTop = end.y + rect.width / 2 * tag;
                                arrowLeft = end.x + rect.width / 2;
                        }
                        return {
                                x : arrowLeft,
                                y : arrowTop
                        };
                },

                arrow : function(p1, p2, r) {// 画箭头，p1 开始位置,p2 结束位置, r前头的边长
                        var atan = Math.atan2(p1.y - p2.y, p2.x - p1.x) * (180 / Math.PI);

                        var centerX = p2.x - r * Math.cos(atan * (Math.PI / 180));
                        var centerY = p2.y + r * Math.sin(atan * (Math.PI / 180));

                        var x2 = centerX + r * Math.cos((atan + 120) * (Math.PI / 180));
                        var y2 = centerY - r * Math.sin((atan + 120) * (Math.PI / 180));

                        var x3 = centerX + r * Math.cos((atan + 240) * (Math.PI / 180));
                        var y3 = centerY - r * Math.sin((atan + 240) * (Math.PI / 180));
                        return [p2, {
                                                x : x2,
                                                y : y2
                                        }, {
                                                x : x3,
                                                y : y3
                                        }];
                }
        }

        designer.rect = function(o, r) {
                var _this = this, _uid = designer.util.nextId(), _o = snaker.extend(true, {},
                                designer.config.rect, o), _id = 'rect' + _uid, _r = r, // Raphael画笔
                _rect, _img, // 图标
                _name, // 状态名称
                _text, // 显示文本
                _ox, _oy; // 拖动时，保存起点位置;
                _o.text.text += _uid;

                _rect = _r.rect(_o.attr.x, _o.attr.y, _o.attr.width, _o.attr.height,
                                _o.attr.r).hide().attr(_o.attr);

                _img = _r.image(designer.config.basePath + _o.img.src,
                                _o.attr.x + _o.img.width / 2,
                                _o.attr.y + (_o.attr.height - _o.img.height) / 2, _o.img.width,
                                _o.img.height).hide();
                _name = _r.text(
                                _o.attr.x + _o.img.width + (_o.attr.width - _o.img.width) / 2,
                                _o.attr.y + designer.config.lineHeight / 2, _o.name.text).hide()
                                .attr(_o.name);
                _text = _r.text(
                                _o.attr.x + _o.img.width + (_o.attr.width - _o.img.width) / 2,
                                _o.attr.y + (_o.attr.height - designer.config.lineHeight) / 2
                                                + designer.config.lineHeight, _o.text.text).hide()
                                .attr(_o.text);// 文本

                // 拖动处理----------------------------------------
                _rect.drag(function(dx, dy) {
                                        dragMove(dx, dy);
                                }, function() {
                                        dragStart()
                                }, function() {
                                        dragUp();
                                });
                _img.drag(function(dx, dy) {
                                        dragMove(dx, dy);
                                }, function() {
                                        dragStart()
                                }, function() {
                                        dragUp();
                                });
                _name.drag(function(dx, dy) {
                                        dragMove(dx, dy);
                                }, function() {
                                        dragStart()
                                }, function() {
                                        dragUp();
                                });
                _text.drag(function(dx, dy) {
                                        dragMove(dx, dy);
                                }, function() {
                                        dragStart()
                                }, function() {
                                        dragUp();
                                });

                var dragMove = function(dx, dy) {// 拖动中
                        if (!designer.config.editable)
                                return;

                        var x = (_ox + dx);// -((_ox+dx)%10);
                        var y = (_oy + dy);// -((_oy+dy)%10);

                        _bbox.x = x - _o.margin;
                        _bbox.y = y - _o.margin;
                        resize();
                };

                var dragStart = function() {// 开始拖动
                        _ox = _rect.attr("x");
                        _oy = _rect.attr("y");
                        _rect.attr({
                                                opacity : 0.5
                                        });
                        _img.attr({
                                                opacity : 0.5
                                        });
                        _text.attr({
                                                opacity : 0.5
                                        });
                };

                var dragUp = function() {// 拖动结束
                        _rect.attr({
                                                opacity : 1
                                        });
                        _img.attr({
                                                opacity : 1
                                        });
                        _text.attr({
                                                opacity : 1
                                        });
                };

                // 改变大小的边框
                var _bpath, _bdots = {}, _bw = 5, _bbox = {
                        x : _o.attr.x - _o.margin,
                        y : _o.attr.y - _o.margin,
                        width : _o.attr.width + _o.margin * 2,
                        height : _o.attr.height + _o.margin * 2
                };

                _bpath = _r.path('M0 0L1 1').hide();
                _bdots['t'] = _r.rect(0, 0, _bw, _bw).attr({
                                        fill : '#000',
                                        stroke : '#fff',
                                        cursor : 's-resize'
                                }).hide().drag(function(dx, dy) {
                                        bdragMove(dx, dy, 't');
                                }, function() {
                                        bdragStart(this.attr('x') + _bw / 2, this.attr('y') + _bw
                                                                        / 2, 't');
                                }, function() {
                                });// 上
                _bdots['lt'] = _r.rect(0, 0, _bw, _bw).attr({
                                        fill : '#000',
                                        stroke : '#fff',
                                        cursor : 'nw-resize'
                                }).hide().drag(function(dx, dy) {
                                        bdragMove(dx, dy, 'lt');
                                }, function() {
                                        bdragStart(this.attr('x') + _bw / 2, this.attr('y') + _bw
                                                                        / 2, 'lt');
                                }, function() {
                                });// 左上
                _bdots['l'] = _r.rect(0, 0, _bw, _bw).attr({
                                        fill : '#000',
                                        stroke : '#fff',
                                        cursor : 'w-resize'
                                }).hide().drag(function(dx, dy) {
                                        bdragMove(dx, dy, 'l');
                                }, function() {
                                        bdragStart(this.attr('x') + _bw / 2, this.attr('y') + _bw
                                                                        / 2, 'l');
                                }, function() {
                                });// 左
                _bdots['lb'] = _r.rect(0, 0, _bw, _bw).attr({
                                        fill : '#000',
                                        stroke : '#fff',
                                        cursor : 'sw-resize'
                                }).hide().drag(function(dx, dy) {
                                        bdragMove(dx, dy, 'lb');
                                }, function() {
                                        bdragStart(this.attr('x') + _bw / 2, this.attr('y') + _bw
                                                                        / 2, 'lb');
                                }, function() {
                                });// 左下
                _bdots['b'] = _r.rect(0, 0, _bw, _bw).attr({
                                        fill : '#000',
                                        stroke : '#fff',
                                        cursor : 's-resize'
                                }).hide().drag(function(dx, dy) {
                                        bdragMove(dx, dy, 'b');
                                }, function() {
                                        bdragStart(this.attr('x') + _bw / 2, this.attr('y') + _bw
                                                                        / 2, 'b');
                                }, function() {
                                });// 下
                _bdots['rb'] = _r.rect(0, 0, _bw, _bw).attr({
                                        fill : '#000',
                                        stroke : '#fff',
                                        cursor : 'se-resize'
                                }).hide().drag(function(dx, dy) {
                                        bdragMove(dx, dy, 'rb');
                                }, function() {
                                        bdragStart(this.attr('x') + _bw / 2, this.attr('y') + _bw
                                                                        / 2, 'rb');
                                }, function() {
                                });// 右下
                _bdots['r'] = _r.rect(0, 0, _bw, _bw).attr({
                                        fill : '#000',
                                        stroke : '#fff',
                                        cursor : 'w-resize'
                                }).hide().drag(function(dx, dy) {
                                        bdragMove(dx, dy, 'r');
                                }, function() {
                                        bdragStart(this.attr('x') + _bw / 2, this.attr('y') + _bw
                                                                        / 2, 'r')
                                }, function() {
                                });// 右
                _bdots['rt'] = _r.rect(0, 0, _bw, _bw).attr({
                                        fill : '#000',
                                        stroke : '#fff',
                                        cursor : 'ne-resize'
                                }).hide().drag(function(dx, dy) {
                                        bdragMove(dx, dy, 'rt');
                                }, function() {
                                        bdragStart(this.attr('x') + _bw / 2, this.attr('y') + _bw
                                                                        / 2, 'rt')
                                }, function() {
                                });// 右上
                $([_bdots['t'].node,_bdots['lt'].node,_bdots['l'].node,_bdots['lb'].node,_bdots['b'].node,_bdots['rb'].node,_bdots['r'].node,_bdots['rt'].node]).click(function(){return false;});

                var bdragMove = function(dx, dy, t) {
                        if (!designer.config.editable)
                                return;
                        var x = _bx + dx, y = _by + dy;
                        switch (t) {
                                case 't' :
                                        _bbox.height += _bbox.y - y;
                                        _bbox.y = y;
                                        break;
                                case 'lt' :
                                        _bbox.width += _bbox.x - x;
                                        _bbox.height += _bbox.y - y;
                                        _bbox.x = x;
                                        _bbox.y = y;
                                        break;
                                case 'l' :
                                        _bbox.width += _bbox.x - x;
                                        _bbox.x = x;
                                        break;
                                case 'lb' :
                                        _bbox.height = y - _bbox.y;
                                        _bbox.width += _bbox.x - x;
                                        _bbox.x = x;
                                        break;
                                case 'b' :
                                        _bbox.height = y - _bbox.y;
                                        break;
                                case 'rb' :
                                        _bbox.height = y - _bbox.y;
                                        _bbox.width = x - _bbox.x;
                                        break;
                                case 'r' :
                                        _bbox.width = x - _bbox.x;
                                        break;
                                case 'rt' :
                                        _bbox.width = x - _bbox.x;
                                        _bbox.height += _bbox.y - y;
                                        _bbox.y = y;
                                        break;
                        }
                        resize();
                        // $('body').append(t);
                };
                var bdragStart = function(ox, oy, t) {
                        _bx = ox;
                        _by = oy;
                };

                // 事件处理--------------------------------
                snaker([_rect.node, _text.node, _name.node, _img.node]).bind('click',
                                function() {
                                        if (!designer.config.editable)
                                                return;

                                        showBox();
                                        var mod = snaker(_r).data('mod');
                                        switch (mod) {
                                                case 'pointer' :
                                                        break;
                                                case 'path' :
                                                        var pre = snaker(_r).data('currNode');
                                                        if (pre && pre.getId() != _id
                                                                        && pre.getId().substring(0, 4) == 'rect') {
                                                        	snaker(_r).trigger('addpath', [pre, _this]);
                                                        }
                                                        break;
                                        }
                                        snaker(_r).trigger('click', _this);
                                        snaker(_r).data('currNode', _this);
                                        return false;
                                });

                var clickHandler = function(e, src) {
                        if (!designer.config.editable)
                                return;
                        if (src.getId() == _id) {
                        	snaker(_r).trigger('showprops', [_o.props, src]);
                        } else {
                                hideBox();
                        }
                };
                snaker(_r).bind('click', clickHandler);

                var textchangeHandler = function(e, text, src) {
                        if (src.getId() == _id) {
                                _text.attr({
                                                        text : text
                                                });
                        }
                };
                snaker(_r).bind('textchange', textchangeHandler);

                // 私有函数-----------------------
                // 边框路径
                function getBoxPathString() {
                        return 'M' + _bbox.x + ' ' + _bbox.y + 'L' + _bbox.x + ' '
                                        + (_bbox.y + _bbox.height) + 'L' + (_bbox.x + _bbox.width)
                                        + ' ' + (_bbox.y + _bbox.height) + 'L'
                                        + (_bbox.x + _bbox.width) + ' ' + _bbox.y + 'L' + _bbox.x
                                        + ' ' + _bbox.y;
                }
                // 显示边框
                function showBox() {
                        _bpath.show();
                        for (var k in _bdots) {
                                _bdots[k].show();
                        }
                }
                // 隐藏
                function hideBox() {
                        _bpath.hide();
                        for (var k in _bdots) {
                                _bdots[k].hide();
                        }
                }

                // 根据_bbox，更新位置信息
                function resize() {
                        var rx = _bbox.x + _o.margin, ry = _bbox.y + _o.margin, rw = _bbox.width
                                        - _o.margin * 2, rh = _bbox.height - _o.margin * 2;

                        _rect.attr({
                                                x : rx,
                                                y : ry,
                                                width : rw,
                                                height : rh
                                        });
                        switch (_o.showType) {
                                case 'image' :
                                        _img.attr({
                                                                x : rx + (rw - _o.img.width) / 2,
                                                                y : ry + (rh - _o.img.height) / 2
                                                        }).show();
                                        break;
                                case 'text' :
                                        _rect.show();
                                        _text.attr({
                                                                x : rx + rw / 2,
                                                                y : ry + rh / 2
                                                        }).show();// 文本
                                        break;
                                case 'image&text' :
                                        _rect.show();
                                        _name.attr({
                                                                x : rx + _o.img.width + (rw - _o.img.width) / 2,
                                                                y : ry + designer.config.lineHeight / 2
                                                        }).show();
                                        _text.attr({
                                                x : rx + _o.img.width + (rw - _o.img.width) / 2,
                                                y : ry + (rh - designer.config.lineHeight) / 2
                                                                + designer.config.lineHeight
                                        }).show();// 文本
                                        _img.attr({
                                                                x : rx + _o.img.width / 2,
                                                                y : ry + (rh - _o.img.height) / 2
                                                        }).show();
                                        break;
                        }

                        _bdots['t'].attr({
                                                x : _bbox.x + _bbox.width / 2 - _bw / 2,
                                                y : _bbox.y - _bw / 2
                                        });// 上
                        _bdots['lt'].attr({
                                                x : _bbox.x - _bw / 2,
                                                y : _bbox.y - _bw / 2
                                        });// 左上
                        _bdots['l'].attr({
                                                x : _bbox.x - _bw / 2,
                                                y : _bbox.y - _bw / 2 + _bbox.height / 2
                                        });// 左
                        _bdots['lb'].attr({
                                                x : _bbox.x - _bw / 2,
                                                y : _bbox.y - _bw / 2 + _bbox.height
                                        });// 左下
                        _bdots['b'].attr({
                                                x : _bbox.x - _bw / 2 + _bbox.width / 2,
                                                y : _bbox.y - _bw / 2 + _bbox.height
                                        });// 下
                        _bdots['rb'].attr({
                                                x : _bbox.x - _bw / 2 + _bbox.width,
                                                y : _bbox.y - _bw / 2 + _bbox.height
                                        });// 右下
                        _bdots['r'].attr({
                                                x : _bbox.x - _bw / 2 + _bbox.width,
                                                y : _bbox.y - _bw / 2 + _bbox.height / 2
                                        });// 右
                        _bdots['rt'].attr({
                                                x : _bbox.x - _bw / 2 + _bbox.width,
                                                y : _bbox.y - _bw / 2
                                        });// 右上
                        _bpath.attr({
                                                path : getBoxPathString()
                                        });

                        snaker(_r).trigger('rectresize', _this);
                };

                // 函数----------------
                // 转化json字串
                this.toJson = function() {
                        var data = "{type:'" + _o.type + "',text:{text:'"
                                        + _text.attr('text') + "'}, attr:{ x:"
                                        + Math.round(_rect.attr('x')) + ", y:"
                                        + Math.round(_rect.attr('y')) + ", width:"
                                        + Math.round(_rect.attr('width')) + ", height:"
                                        + Math.round(_rect.attr('height')) + "}, props:{";
                        for (var k in _o.props) {
                                data += k + ":{value:'"
                                                + _o.props[k].value + "'},";
                        }
                        if (data.substring(data.length - 1, data.length) == ',')
                                data = data.substring(0, data.length - 1);
                        data += "}}";
                        return data;
                };
				this.toBeforeXml=function(){
					var data="<"+_o.type+" layout=\""+(Math.round(_rect.attr("x"))-120)+","+Math.round(_rect.attr("y"))+","+Math.round(_rect.attr("width"))+","+Math.round(_rect.attr("height"))+"\" ";
					for(var k in _o.props){
						if(k=="name"&&_o.props[k].value==""){
							alert(_o.type+" 名称 不能为空");
							return ""
						}
						if(k=="layout") continue;
						if(_o.props[k].value!=""){
							data+=k+"=\""+_o.props[k].value+"\" "
						}
					}
					data+=">";
					return data
				};
				this.toAfterXml=function(){
					var data="</"+_o.type+">";
					return data
				};
                // 从数据中恢复图
                this.restore = function(data) {
                        var obj = data;
                        // if (typeof data === 'string')
                        // obj = eval(data);

                        _o = snaker.extend(true, _o, data);

                        _text.attr({
                                                text : obj.text.text
                                        });
                        resize();
                };
				this.getName=function(){
					for(var k in _o.props){
						if(k=="name"){
							return _o.props[k].value;
						}
					}
				}
                this.getBBox = function() {
                        return _bbox;
                };
                this.getId = function() {
                        return _id;
                };
                this.remove = function() {
                        _rect.remove();
                        _text.remove();
                        _name.remove();
                        _img.remove();
                        _bpath.remove();
                        for (var k in _bdots) {
                                _bdots[k].remove();
                        }
                };
                this.text = function() {
                        return _text.attr('text');
                };
                this.attr = function(attr) {
                        if (attr)
                                _rect.attr(attr);
                };

                resize();// 初始化位置
        };

        designer.path = function(o, r, from, to) {
                var _this = this, _r = r, _o = snaker.extend(true, {}, designer.config.path), _path, _arrow, _text, _textPos = _o.text.textPos, _ox, _oy, _from = from, _to = to, _id = 'path'
                                + designer.util.nextId(), _dotList, _autoText = true;
                // 点
                function dot(type, pos, left, right) {
                        var _this = this, _t = type, _n, _lt = left, _rt = right, _ox, _oy, // 缓存移动前时位置
                        _pos = pos;// 缓存位置信息{x,y}, 注意：这是计算出中心点

                        switch (_t) {
                                case 'from' :
                                        _n = _r.rect(pos.x - _o.attr.fromDot.width / 2,
                                                        pos.y - _o.attr.fromDot.height / 2,
                                                        _o.attr.fromDot.width, _o.attr.fromDot.height)
                                                        .attr(_o.attr.fromDot);
                                        break;
                                case 'big' :
                                        _n = _r.rect(pos.x - _o.attr.bigDot.width / 2,
                                                        pos.y - _o.attr.bigDot.height / 2,
                                                        _o.attr.bigDot.width, _o.attr.bigDot.height)
                                                        .attr(_o.attr.bigDot);
                                        break;
                                case 'small' :
                                        _n = _r.rect(pos.x - _o.attr.smallDot.width / 2,
                                                        pos.y - _o.attr.smallDot.height / 2,
                                                        _o.attr.smallDot.width, _o.attr.smallDot.height)
                                                        .attr(_o.attr.smallDot);
                                        break;
                                case 'to' :
                                        _n = _r.rect(pos.x - _o.attr.toDot.width / 2,
                                                        pos.y - _o.attr.toDot.height / 2,
                                                        _o.attr.toDot.width, _o.attr.toDot.height)
                                                        .attr(_o.attr.toDot);

                                        break;
                        }
                        if (_n && (_t == 'big' || _t == 'small')) {
                                _n.drag(function(dx, dy) {
                                                        dragMove(dx, dy);
                                                }, function() {
                                                        dragStart()
                                                }, function() {
                                                        dragUp();
                                                });// 初始化拖动
                                var dragMove = function(dx, dy) {// 拖动中
                                        var x = (_ox + dx), y = (_oy + dy);
                                        _this.moveTo(x, y);
                                };

                                var dragStart = function() {// 开始拖动
                                        if (_t == 'big') {
                                                _ox = _n.attr("x") + _o.attr.bigDot.width / 2;
                                                _oy = _n.attr("y") + _o.attr.bigDot.height / 2;
                                        }
                                        if (_t == 'small') {
                                                _ox = _n.attr("x") + _o.attr.smallDot.width / 2;
                                                _oy = _n.attr("y") + _o.attr.smallDot.height / 2;
                                        }
                                };

                                var dragUp = function() {// 拖动结束

                                };
                        }
                        //snaker(_n.node).click(function(){return false;});

                        this.type = function(t) {
                                if (t)
                                        _t = t;
                                else
                                        return _t;
                        };
                        this.node = function(n) {
                                if (n)
                                        _n = n;
                                else
                                        return _n;
                        };
                        this.left = function(l) {
                                if (l)
                                        _lt = l;
                                else
                                        return _lt;
                        };
                        this.right = function(r) {
                                if (r)
                                        _rt = r;
                                else
                                        return _rt;
                        };
                        this.remove = function() {
                                _lt = null;
                                _rt = null;
                                _n.remove();
                        };
                        this.pos = function(pos) {
                                if (pos) {
                                        _pos = pos;
                                        _n.attr({
                                                                x : _pos.x - _n.attr('width') / 2,
                                                                y : _pos.y - _n.attr('height') / 2
                                                        });
                                        return this;
                                } else {
                                        return _pos
                                }
                        };

                        this.moveTo = function(x, y) {
                                this.pos({
                                                        x : x,
                                                        y : y
                                                });

                                switch (_t) {
                                        case 'from' :
                                                if (_rt && _rt.right() && _rt.right().type() == 'to') {
                                                        _rt.right().pos(designer.util.connPoint(
                                                                        _to.getBBox(), _pos));
                                                }
                                                if (_rt && _rt.right()) {
                                                        _rt
                                                                        .pos(designer.util.center(_pos, _rt.right()
                                                                                                        .pos()));
                                                }
                                                break;
                                        case 'big' :

                                                if (_rt && _rt.right() && _rt.right().type() == 'to') {
                                                        _rt.right().pos(designer.util.connPoint(
                                                                        _to.getBBox(), _pos));
                                                }
                                                if (_lt && _lt.left() && _lt.left().type() == 'from') {
                                                        _lt.left().pos(designer.util.connPoint(_from
                                                                                        .getBBox(), _pos));
                                                }
                                                if (_rt && _rt.right()) {
                                                        _rt
                                                                        .pos(designer.util.center(_pos, _rt.right()
                                                                                                        .pos()));
                                                }
                                                if (_lt && _lt.left()) {
                                                        _lt.pos(designer.util.center(_pos, _lt.left().pos()));
                                                }
                                                // 三个大点在一条线上，移除中间的小点
                                                var pos = {
                                                        x : _pos.x,
                                                        y : _pos.y
                                                };
                                                if (designer.util.isLine(_lt.left().pos(), pos, _rt
                                                                                .right().pos())) {
                                                        _t = 'small';
                                                        _n.attr(_o.attr.smallDot);
                                                        this.pos(pos);
                                                        var lt = _lt;
                                                        _lt.left().right(_lt.right());
                                                        _lt = _lt.left();
                                                        lt.remove();
                                                        var rt = _rt;
                                                        _rt.right().left(_rt.left());
                                                        _rt = _rt.right();
                                                        rt.remove();
                                                        // $('body').append('ok.');
                                                }
                                                break;
                                        case 'small' :// 移动小点时，转变为大点，增加俩个小点
                                                if (_lt && _rt && !designer.util.isLine(_lt.pos(), {
                                                                        x : _pos.x,
                                                                        y : _pos.y
                                                                }, _rt.pos())) {

                                                        _t = 'big';

                                                        _n.attr(_o.attr.bigDot);
                                                        var lt = new dot('small', designer.util.center(_lt
                                                                                                        .pos(), _pos), _lt, _lt
                                                                                        .right());
                                                        _lt.right(lt);
                                                        _lt = lt;

                                                        var rt = new dot('small', designer.util.center(_rt
                                                                                                        .pos(), _pos), _rt.left(),
                                                                        _rt);
                                                        _rt.left(rt);
                                                        _rt = rt;

                                                }
                                                break;
                                        case 'to' :
                                                if (_lt && _lt.left() && _lt.left().type() == 'from') {
                                                        _lt.left().pos(designer.util.connPoint(_from
                                                                                        .getBBox(), _pos));
                                                }
                                                if (_lt && _lt.left()) {
                                                        _lt.pos(designer.util.center(_pos, _lt.left().pos()));
                                                }
                                                break;
                                }
                                refreshpath();
                        };
                }
                function dotList() {
                        // if(!_from) throw '没有from节点!';
                        var _fromDot, _toDot, _fromBB = _from.getBBox(), _toBB = _to
                                        .getBBox(), _fromPos, _toPos;

                        _fromPos = designer.util.connPoint(_fromBB, {
                                                x : _toBB.x + _toBB.width / 2,
                                                y : _toBB.y + _toBB.height / 2
                                        });
                        _toPos = designer.util.connPoint(_toBB, _fromPos);

                        _fromDot = new dot('from', _fromPos, null, new dot('small', {
                                                                x : (_fromPos.x + _toPos.x) / 2,
                                                                y : (_fromPos.y + _toPos.y) / 2
                                                        }));
                        _fromDot.right().left(_fromDot);
                        _toDot = new dot('to', _toPos, _fromDot.right(), null);
                        _fromDot.right().right(_toDot);

                        // 转换为path格式的字串
                        this.toPathString = function() {
                                if (!_fromDot)
                                        return "";

                                var d = _fromDot, p = "M" + d.pos().x + " " + d.pos().y, arr = "";
                                // 线的路径
                                while (d.right()) {
                                        d = d.right();
                                        p += "L" + d.pos().x + " " + d.pos().y;
                                }
                                // 箭头路径
                                var arrPos = designer.util.arrow(d.left().pos(), d.pos(),
                                                _o.attr.arrow.radius);
                                arr = "M" + arrPos[0].x + " " + arrPos[0].y + "L" + arrPos[1].x
                                                + " " + arrPos[1].y + "L" + arrPos[2].x + " "
                                                + arrPos[2].y + "z";
                                return [p, arr];
                        };
                        this.toJson = function() {
                                var data = "[", d = _fromDot;

                                while (d) {
                                        if (d.type() == 'big')
                                                data += "{x:" + Math.round(d.pos().x) + ",y:"
                                                                + Math.round(d.pos().y) + "},";
                                        d = d.right();
                                }
                                if (data.substring(data.length - 1, data.length) == ',')
                                        data = data.substring(0, data.length - 1);
                                data += "]";
                                return data;
                        };
						this.toXml=function(){
							var data = "", d = _fromDot;
							while(d){
								if(d.type()=="big"){
									data+=(Math.round(d.pos().x)-120)+","+Math.round(d.pos().y)+";"
								}
								d=d.right()
							}
							if(data.substring(data.length-1,data.length)==";"){
								data=data.substring(0,data.length-1)
							}
							return data
						};
                        this.restore = function(data) {
                                var obj = data, d = _fromDot.right();

                                for (var i = 0; i < obj.length; i++) {
                                        d.moveTo(obj[i].x, obj[i].y);
                                        d.moveTo(obj[i].x, obj[i].y);
                                        d = d.right();
                                }

                                this.hide();
                        };

                        this.fromDot = function() {
                                return _fromDot;
                        };
                        this.toDot = function() {
                                return _toDot;
                        };
                        this.midDot = function() {// 返回中间点
                                var mid = _fromDot.right(), end = _fromDot.right().right();
                                while (end.right() && end.right().right()) {
                                        end = end.right().right();
                                        mid = mid.right();
                                }
                                return mid;
                        };
                        this.show = function() {
                                var d = _fromDot;
                                while (d) {
                                        d.node().show();
                                        d = d.right();
                                }
                        };
                        this.hide = function() {
                                var d = _fromDot;
                                while (d) {
                                        d.node().hide();
                                        d = d.right();
                                }
                        };
                        this.remove = function() {
                                var d = _fromDot;
                                while (d) {
                                        if (d.right()) {
                                                d = d.right();
                                                d.left().remove();
                                        } else {
                                                d.remove();
                                                d = null;
                                        }
                                }
                        };
                }
                // 初始化操作
                _o = snaker.extend(true, _o, o);
                _path = _r.path(_o.attr.path.path).attr(_o.attr.path);
                _arrow = _r.path(_o.attr.arrow.path).attr(_o.attr.arrow);

                _dotList = new dotList();
                _dotList.hide();

                _text = _r.text(0, 0, _o.text.text).attr(_o.text).attr({
                	text:_o.text.text.replace("{from}",_from.text()).replace("{to}",_to.text())
                });
                _text.drag(function(dx, dy) {
                                        if (!designer.config.editable)
                                                return;
                                        _text.attr({
                                                                x : _ox + dx,
                                                                y : _oy + dy
                                                        });
                                }, function() {
                                        _ox = _text.attr('x');
                                        _oy = _text.attr('y');
                                }, function() {
                                        var mid = _dotList.midDot().pos();
                                        _textPos = {
                                                x : _text.attr('x') - mid.x,
                                                y : _text.attr('y') - mid.y
                                        };
                                });
                refreshpath();// 初始化路径

                // 事件处理--------------------
                snaker([_path.node, _arrow.node]).bind('click', function() {
                                        if (!designer.config.editable)
                                                return;
                                        snaker(_r).trigger('click', _this);
                                        snaker(_r).data('currNode', _this);
                                        return false;
                                });

                // 处理点击事件，线或矩形
                var clickHandler = function(e, src) {
                        if (!designer.config.editable)
                                return;
                        if (src && src.getId() == _id) {
                                _dotList.show();
                                snaker(_r).trigger('showprops', [_o.props, _this]);
                        } else {
                                _dotList.hide();
                        }

                        var mod = snaker(_r).data('mod');
                        switch (mod) {
                                case 'pointer' :

                                        break;
                                case 'path' :

                                        break;
                        }

                };
                snaker(_r).bind('click', clickHandler);

                // 删除事件处理
                var removerectHandler = function(e, src) {
                        if (!designer.config.editable)
                                return;
                        if (src
                                        && (src.getId() == _from.getId() || src.getId() == _to
                                                        .getId())) {
                                // _this.remove();
                        	snaker(_r).trigger('removepath', _this);
                        }
                };
                snaker(_r).bind('removerect', removerectHandler);

                // 矩形移动时间处理
                var rectresizeHandler = function(e, src) {
                	alert("rectresize");
                        if (!designer.config.editable)
                                return;
                        if (_from && _from.getId() == src.getId()) {
                                var rp;
                                if (_dotList.fromDot().right().right().type() == 'to') {
                                        rp = {
                                                x : _to.getBBox().x + _to.getBBox().width / 2,
                                                y : _to.getBBox().y + _to.getBBox().height / 2
                                        };
                                } else {
                                        rp = _dotList.fromDot().right().right().pos();
                                }
                                var p = designer.util.connPoint(_from.getBBox(), rp);
                                _dotList.fromDot().moveTo(p.x, p.y);
                                refreshpath();
                        }
                        if (_to && _to.getId() == src.getId()) {
                                var rp;
                                if (_dotList.toDot().left().left().type() == 'from') {
                                        rp = {
                                                x : _from.getBBox().x + _from.getBBox().width / 2,
                                                y : _from.getBBox().y + _from.getBBox().height / 2
                                        };
                                } else {
                                        rp = _dotList.toDot().left().left().pos();
                                }
                                var p = designer.util.connPoint(_to.getBBox(), rp);
                                _dotList.toDot().moveTo(p.x, p.y);
                                refreshpath();
                        }
                };
                snaker(_r).bind('rectresize', rectresizeHandler);
                
                var textchangeHandler = function(e, v, src) {
                        if (src.getId() == _id) {// 改变自身文本
                                _text.attr({
                                                        text : v
                                                });
                                //_autoText = false;
                        }
                        //$('body').append('['+_autoText+','+_text.attr('text')+','+src.getId()+','+_to.getId()+']');
                        /**if (_autoText) {
                                if (_to.getId() == src.getId()){
                                        //$('body').append('change!!!');
                                        _text.attr({
                                                                text : _o.text.patten.replace('{from}',
                                                                                _from.text()).replace('{to}', v)
                                                        });
                                }
                                else if (_from.getId() == src.getId()){
                                        //$('body').append('change!!!');
                                        _text.attr({
                                                                text : _o.text.patten.replace('{from}', v)
                                                                                .replace('{to}', _to.text())
                                                        });
                                }
                        }**/
                };
                snaker(_r).bind('textchange', textchangeHandler);

                // 函数-------------------------------------------------
                this.from = function() {
                        return _from;
                };
                this.to = function() {
                        return _to;
                };
                // 转化json数据
                this.toJson = function() {
                        var data = "{from:'" + _from.getId() + "',to:'" + _to.getId()
                                        + "', dots:" + _dotList.toJson() + ",text:{text:'"
                                        + _text.attr('text') + "',textPos:{x:"
                                        + Math.round(_textPos.x) + ",y:" + Math.round(_textPos.y)
                                        + "}}, props:{";
                        for (var k in _o.props) {
                                data += k + ":{value:'"
                                                + _o.props[k].value + "'},";
                        }
                        if (data.substring(data.length - 1, data.length) == ',')
                                data = data.substring(0, data.length - 1);
                        data += '}}';
                        return data;
                };
				this.toXml=function(){
					var data="<transition offset=\""+(Math.round(_textPos.x)-120)+","+Math.round(_textPos.y)+"\" to=\""+_to.getName()+"\" ";
					var dots=_dotList.toXml();
					if(dots!="") data+=" g=\""+dots+"\" ";
					for(var k in _o.props){
						if(k=="name"&&_o.props[k].value==""){
							data+=k+"=\""+_id+"\" ";
							continue;
						}
						if(_o.props[k].value!=""){
							data+=k+"=\""+_o.props[o].value+"\" ";
						}
					}
					data+="/>";
					return data;
				};
                // 恢复
                this.restore = function(data) {
                        var obj = data;
                        _o.props.displayName.value=obj.text.text;
                        _o = snaker.extend(true, _o, data);
                        //$('body').append('['+_text.attr('text')+','+_o.text.text+']');
                        //if(_text.attr('text')!=_o.text.text){
                                 //$('body').append('['+_text.attr('text')+','+_o.text.text+']');
                             //_text.attr({text:_o.text.text});
                             //_autoText = false;
                        //}

                        _dotList.restore(obj.dots);
                };
                // 删除
                this.remove = function() {
                        _dotList.remove();
                        _path.remove();
                        _arrow.remove();
                        _text.remove();
                        try {
                        	snaker(_r).unbind('click', clickHandler);
                        } catch (e) {
                        }
                        try {
                        	snaker(_r).unbind('removerect', removerectHandler);
                        } catch (e) {
                        }
                        try {
                        	snaker(_r).unbind('rectresize', rectresizeHandler);;
                        } catch (e) {
                        }
                        try {
                        	snaker(_r).unbind('textchange', textchangeHandler);
                        } catch (e) {
                        }
                };
                // 刷新路径
                function refreshpath() {
                	alert("refresh");
                        var p = _dotList.toPathString(), mid = _dotList.midDot().pos();
                        _path.attr({
                                                path : p[0]
                                        });
                        _arrow.attr({
                                                path : p[1]
                                        });
                        _text.attr({
                                                x : mid.x + _textPos.x,
                                                y : mid.y + _textPos.y
                                        });
                        // $('body').append('refresh.');
                }

                this.getId = function() {
                        return _id;
                };
                this.text = function() {
                        return _text.attr('text');
                };
                this.attr = function(attr) {
                        if (attr && attr.path)
                                _path.attr(attr.path);
                        if (attr && attr.arrow)
                                _arrow.attr(attr.arrow);
                        // $('body').append('aaaaaa');
                };

        };

        designer.props = function(o, r) {
                var _this = this, _pdiv = snaker('#properties').hide()/*.draggable({
                                        handle : '#properties_handle'
                                }).resizable().css(designer.config.props.attr)*/.bind('click',
                                function() {
                                        return false;
                                }), _tb = _pdiv.find('table'), _r = r, _src;

                var showpropsHandler = function(e, props, src) {
                        if (_src && _src.getId() == src.getId()) {// 连续点击不刷新
                                return;
                        }
                        _src = src;
                        snaker(_tb).find('.editor').each(function() {
                                                var e = snaker(this).data('editor');
                                                if (e)
                                                        e.destroy();
                                        });

                        _tb.empty();
                        _pdiv.show();
                        for (var k in props) {
								if(props[k].name=="name"&&props[k].value==""){
									props[k].value=src.getId()
								}
								props[k].value=props[k].value.replace(new RegExp("@@","gm"), "'");
								if(!props[k].label){
									continue;
								}
                                _tb.append('<tr><td class="properties_name">' + props[k].label + '</td><td class="properties_value"><div id="p'
                                                + k + '" class="editor"></div></td></tr>');
                                if (props[k].editor)
                                        props[k].editor().init(props, k, 'p' + k, src, _r);
                                // $('body').append(props[i].editor+'a');
                        }
                };
                snaker(_r).bind('showprops', showpropsHandler);

        };

        // 属性编辑器
        designer.editors = {
                textEditor : function() {
                        var _props, _k, _div, _src, _r;
                        this.init = function(props, k, div, src, r) {
                                _props = props;
                                _k = k;
                                _div = div;
                                _src = src;
                                _r = r;

                                snaker('<input  style="width:98%;"/>').val(_src.text()).change(
                                                function() {
                                                        props[_k].value = snaker(this).val();
                                                        snaker(_r).trigger('textchange', [snaker(this).val(), _src]);
                                                }).appendTo('#' + _div);
                                // $('body').append('aaaa');

                                snaker('#' + _div).data('editor', this);
                        };
                        this.destroy = function() {
                        		snaker('#' + _div + ' input').each(function() {
                                                        _props[_k].value = snaker(this).val();
                                                        snaker(_r).trigger('textchange', [snaker(this).val(), _src]);
                                                });
                                // $('body').append('destroy.');
                        };
                }
        };

        // 初始化流程
        designer.init = function(c, o) {
                var _w = snaker(window).width(), _h = snaker(window).height(), _r = Raphael(c, _w
                                                * 1.5, _h * 1.5), _states = {}, _paths = {};

                snaker.extend(true, designer.config, o);

                /**
                 * 删除： 删除状态时，触发removerect事件，连接在这个状态上当路径监听到这个事件，触发removepath删除自身；
                 * 删除路径时，触发removepath事件
                 */
                snaker(document).keydown(function(arg) {
                        if (!designer.config.editable)
                                return;
                        if (arg.keyCode == 46) {
                                var c = snaker(_r).data('currNode');
                                if (c) {
                                        if (c.getId().substring(0, 4) == 'rect') {
                                        	snaker(_r).trigger('removerect', c);
                                        } else if (c.getId().substring(0, 4) == 'path') {
                                        	snaker(_r).trigger('removepath', c);
                                        }

                                        snaker(_r).removeData('currNode');
                                }
                        }
                                // alert(arg.keyCode);
                        });

                snaker(document).click(function() {
                	snaker(_r).data('currNode', null);
                	snaker(_r).trigger('click', {
                        getId : function() {
                            return '00000000';
                        }
                    });
                	snaker(_r).trigger('showprops', [designer.config.props.props, {
                        getId : function() {
                            return '00000000';
                        }
                    }]);
               });

                // 删除事件
                var removeHandler = function(e, src) {
                        if (!designer.config.editable)
                                return;
                        if (src.getId().substring(0, 4) == 'rect') {
                                _states[src.getId()] = null;
                                src.remove();
                        } else if (src.getId().substring(0, 4) == 'path') {
                                _paths[src.getId()] = null;
                                src.remove();
                        }
                };
                snaker(_r).bind('removepath', removeHandler);
                snaker(_r).bind('removerect', removeHandler);

                // 添加状态
                snaker(_r).bind('addrect', function(e, type, o) {
                        // $('body').append(type+', ');
                        var rect = new designer.rect(snaker.extend(true, {},
                                                        designer.config.tools.states[type], o), _r)
                        _states[rect.getId()] = rect;
                });
                // 添加路径
                var addpathHandler = function(e, from, to) {
                        var path = new designer.path({}, _r, from, to);
                        _paths[path.getId()] = path;
                };
                snaker(_r).bind('addpath', addpathHandler);

                // 模式
                snaker(_r).data('mod', 'point');
                if (designer.config.editable) {
                        // 工具栏
                        /*$("#toolbox").draggable({
                                                handle : '#myflow_tools_handle'
                                        }).css(designer.config.tools.attr);*/

                	snaker('#toolbox div.node').hover(function() {
                		snaker(this).addClass('mover');
                                        }, function() {
                                        	snaker(this).removeClass('mover');
                                        });
                	snaker('#toolbox .selectable').click(function() {
                        	snaker('.selected').removeClass('selected');
                        	snaker(this).addClass('selected');
                        	snaker(_r).data('mod', this.id);

                                        });

                        snaker('#toolbox .state').each(function() {
                        	snaker(this).draggable({
                                                                        helper : 'clone'
                                                                });
                                        });

                        snaker(c).droppable({
                                                accept : '.state',
                                                drop : function(event, ui) {
                                                	snaker(_r).trigger('addrect', [ui.helper.attr('type'), {
                                                                                                attr : {
                                                                                                        x : ui.helper.offset().left,
                                                                                                        y : ui.helper.offset().top
                                                                                                }
                                                                                        }]);
                                                        // $('body').append($(ui).attr('type')+'drop.');
                                                }
                                        });

                        snaker('#save').click(function() {// 保存
							/**
                                                var data = '{states:{';
                                                for (var k in _states) {
                                                        if (_states[k]) {
                                                                data += _states[k].getId() + ':'
                                                                                + _states[k].toJson() + ',';
                                                        }
                                                }
                                                if (data.substring(data.length - 1, data.length) == ',')
                                                        data = data.substring(0, data.length - 1);
                                                data += '},paths:{';
                                                for (var k in _paths) {
                                                        if (_paths[k]) {
                                                                data += _paths[k].getId() + ':'
                                                                                + _paths[k].toJson() + ',';
                                                        }
                                                }
                                                if (data.substring(data.length - 1, data.length) == ',')
                                                        data = data.substring(0, data.length - 1);
                                                data += '},props:{props:{';
                                                for (var k in designer.config.props.props) {
                                                        data += k + ":{value:'"
                                                                        + designer.config.props.props[k].value
                                                                        + "'},";
                                                }
                                                if (data.substring(data.length - 1, data.length) == ',')
                                                        data = data.substring(0, data.length - 1);
                                                data += '}}}';

                                                designer.config.tools.save.onclick(data);
                                                // alert(data);
                                        });*/
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
                        // 属性框
                        new designer.props({}, _r);
                }
                // 恢复
                if (o.restore) {
                        // var data = ((typeof o.restore === 'string') ? eval(o.restore) :
                        // o.restore);
                        var data = o.restore;
                        designer.config.props.props.name.value = data.props.props.name.value;
                        var rmap = {};
                        if (data.states) {
                                for (var k in data.states) {
                                        var rect = new designer.rect(
                                        		snaker
                                                                        .extend(
                                                                                        true,
                                                                                        {},
                                                                                        designer.config.tools.states[data.states[k].type],
                                                                                        data.states[k]), _r);
                                        rect.restore(data.states[k]);
                                        rmap[k] = rect;
                                        _states[rect.getId()] = rect;
                                }
                        }
                        if (data.paths) {
                                for (var k in data.paths) {
                                        var p = new designer.path(snaker.extend(true, {},
                                                                        designer.config.tools.path, data.paths[k]),
                                                        _r, rmap[data.paths[k].from],
                                                        rmap[data.paths[k].to]);
                                        p.restore(data.paths[k]);
                                        _paths[p.getId()] = p;
                                }
                        }
						if(data.props&&data.props.props){
							for(var s in designer.config.props.props){
								var tmp=designer.config.props.props[s];
								for(var ss in data.props.props){
									if(tmp.name==data.props.props[ss].name){
										tmp.value=data.props.props[ss].value;
										break;
									}
								}
							}
						}
                }
                // 历史状态
                var hr = designer.config.historyRects, ar = designer.config.activeRects;
                if (hr.rects.length || ar.rects.length) {
                        var pmap = {}, rmap = {};
                        for (var pid in _paths) {// 先组织MAP
                                if (!rmap[_paths[pid].from().text()]) {
                                        rmap[_paths[pid].from().text()] = {
                                                rect : _paths[pid].from(),
                                                paths : {}
                                        };
                                }
                                rmap[_paths[pid].from().text()].paths[_paths[pid].text()] = _paths[pid];
                                if (!rmap[_paths[pid].to().text()]) {
                                        rmap[_paths[pid].to().text()] = {
                                                rect : _paths[pid].to(),
                                                paths : {}
                                        };
                                }
                        }
                        for (var i = 0; i < hr.rects.length; i++) {
                                if (rmap[hr.rects[i].name]) {
                                        rmap[hr.rects[i].name].rect.attr(hr.rectAttr);
                                }
                                for (var j = 0; j < hr.rects[i].paths.length; j++) {
                                        if (rmap[hr.rects[i].name].paths[hr.rects[i].paths[j]]) {
                                                rmap[hr.rects[i].name].paths[hr.rects[i].paths[j]]
                                                                .attr(hr.pathAttr);
                                        }
                                }
                        }
                        for (var i = 0; i < ar.rects.length; i++) {
                                if (rmap[ar.rects[i].name]) {
                                        rmap[ar.rects[i].name].rect.attr(ar.rectAttr);
                                }
                                for (var j = 0; j < ar.rects[i].paths.length; j++) {
                                        if (rmap[ar.rects[i].name].paths[ar.rects[i].paths[j]]) {
                                                rmap[ar.rects[i].name].paths[ar.rects[i].paths[j]]
                                                                .attr(ar.pathAttr);
                                        }
                                }
                        }
                }
        }

        // 添加jquery方法
        snaker.fn.snakerflow = function(o) {
                return this.each(function() {
                                        designer.init(this, o);
                                });
        };

        snaker.snakerflow = designer;
})(jQuery);