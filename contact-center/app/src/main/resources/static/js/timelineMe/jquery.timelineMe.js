/*--------------------------------------------------------------------
 *JAVASCRIPT "timelineMe.js"
 *Version:    0.1.9 - 2016
 *author:     Mickaël Roy
 *website:    http://www.mickaelroy.com
 *Licensed MIT
-----------------------------------------------------------------------*/

(function ($){

    var pluginName = 'timelineMe';

    /**
     * The plugin constructor
     * @param {DOM Element} element The DOM element where plugin is applied
     * @param {Object} options Options passed to the constructor
     */
    function TimelineMe(element, options) {
        // Store a reference to the source element
        this.el = element;

        // Store a jQuery reference  to the source element
        this.$el = $(element);

        // Set a random (and normally unique) id for the object
        this.instanceId = Math.round(new Date().getTime() + (Math.random() * 100));

        // Set the instance options extending the plugin defaults and
        // the options passed by the user
        this.settings = $.extend({}, $.fn[pluginName].defaults, options);

        // Initialize the plugin instance
        this.init();
    }

    /**
     * Set up your Plugin protptype with desired methods.
     * It is a good practice to implement 'init' and 'destroy' methods.
     */
    TimelineMe.prototype = {

        /**
         * Initialize the plugin instance.
         * Set any other attribtes, store any other element reference, register
         * listeners, etc
         *
         * When bind listerners remember to name tag it with your plugin's name.
         * Elements can have more than one listener attached to the same event
         * so you need to tag it to unbind the appropriate listener on destroy:
         *
         * @example
         * this.$someSubElement.on('click.' + pluginName, function() {
         *      // Do something
         * });
         *
         */
        init: function() {
            this.$el.addClass('timeline-me-container');

            if(this.settings.orientation == 'horizontal') {
                this.$el.addClass('timeline-me-horizontal');
                if(this.settings.scrollBar == false)
                    this.$el.addClass('no-x-scroll');
            } else {
                this.$el.addClass('timeline-me-vertical');
                if(this.settings.scrollBar == false)
                    this.$el.addClass('no-y-scroll');
            }

            var timelineWrapper = $('<div class="timeline-me-wrapper">');
            this.$el.append(timelineWrapper);

            var track = $('<div class="timeline-me-track">');
            timelineWrapper.append(track);

            if(this.settings.scrollZones == true) {
                var leftScroll = $('<div class="timeline-me-leftscroll">');
                var rightScroll = $('<div class="timeline-me-rightscroll">');
                timelineWrapper.before(leftScroll);
                timelineWrapper.after(rightScroll);

                bindScrollTo(leftScroll, rightScroll, timelineWrapper);
            }

            if(this.settings.scrollArrows == true) {
                var leftScroll;
                var rightScroll;
                if(this.settings.leftArrowElm == undefined) {
                    leftScroll = $('<span class="timeline-me-leftarrow">');
                } else {
                    leftScroll = $('<span class="timeline-me-leftarrow-c">');
                    leftScroll.html(this.settings.leftArrowElm);
                }
                if(this.settings.rightArrowElm == undefined) {
                    rightScroll = $('<span class="timeline-me-rightarrow">');
                } else {
                    rightScroll = $('<span class="timeline-me-rightarrow-c">');
                    rightScroll.html(this.settings.rightArrowElm);
                }
                timelineWrapper.before(leftScroll);
                timelineWrapper.after(rightScroll);

                bindScrollTo(leftScroll, rightScroll, timelineWrapper);
            }

            if(this.settings.items && this.settings.items.length > 0) {
                this.content = this.settings.items;

                if(hasNumberProperty(this.content, 'relativePosition')) {
                    this._sortItemsPosition(this.content);
                    this._mergeSamePositionItems(this.content);
                    this._calcDiffWithNextItem(this.content);
                }
                this._sortItemsPosition(this.content);
                this._fillItemsPosition(this.content);

                for(var i = 0; i < this.content.length; i++) {
                    track.append(this._createItemElement(this.content[i]));
                    if(this.settings.orientation == 'horizontal') {
                        resolveContainerWidth(track);
                    }
                }

                // once items' DOM elements have been built, we can reposition them
                if(hasNumberProperty(this.content, 'relativePosition')) {
                    this._calcRelativePosRatio(this.content);
                    this.maxRelativeRatio = getMaxPropertyValue(this.content, 'relativePosRatio');
                    this._calcRelativeHeight(this.content, this.maxRelativeRatio);
                    this._addMissingRelativeHeight(this.content);
                }
            }
        },

        /**
         * The 'destroy' method is were you free the resources used by your plugin:
         * references, unregister listeners, etc.
         *
         * Remember to unbind for your event:
         *
         * @example
         * this.$someSubElement.off('.' + pluginName);
         *
         * Above example will remove any listener from your plugin for on the given
         * element.
         */
        destroy: function() {
          // Remove classes added by the plugin
          this.$el.removeClass('timeline-me-container');
          this.$el.removeClass('timeline-me-horizontal');
          this.$el.removeClass('timeline-me-vertical');
          this.$el.removeClass('no-x-scroll');
          this.$el.removeClass('no-y-scroll');
          // Remove child nodes
          this.$el.empty();
          // Remove any attached data from your plugin
          this.$el.removeData();
        },


        // To call a call a pseudo private method:
        //this._pseudoPrivateMethod();

        // To call a real private method from those public methods, you need to use 'call' or 'apply':
        //privateMethod.call(this);

        /**
         * getItem method
         *
         * @example
         * $('#element').pluginName('getItem', index);
         *
         * @return {item} corresponding to the specified index
         */
        getItem: function(index) {
            return this.content[index];
        },

        /**
         * getItemIndex method
         *
         * @example
         * $('#element').pluginName('getItemIndex', item);
         *
         * @return {index} of the corresponding item
         */
        getItemIndex: function(item) {
            return this.settings.items.indexOf(item);
        },

        /**
         * collapse method
         *
         * @example
         * $('#element').pluginName('collapse', 'show');
         */
        collapse: function(method) {
            switch(method) {
                case 'toggle':
                    this.$el.toggleClass('timeline-me-collapsed');
                    break;
                case 'show':
                    this.$el.removeClass('timeline-me-collapsed');
                    break;
                case 'hide':
                default:
                    this.$el.addClass('timeline-me-collapsed');
                    break;
            }
        },

        /**
         * load method
         *
         * @example
         * $('#element').pluginName('load', itemsToAdd);
         */
        load: function(items) {
            if(!items) return;
            if(!this.settings.items) this.settings.items = [];
            if(!$.isArray(items)) items = [items];
            this.settings.items = this.settings.items.concat(items);

            this.destroy();
            this.init();
        },

        /**
         * You can use the name convention functions started with underscore are
         * private. Really calls to functions starting with underscore are
         * filtered, for example:
         *
         *  @example
         *  $('#element').jqueryPlugin('_pseudoPrivateMethod');  // Will not work
         */
        // Method that create ids for items that does not have one
        _fillItemsId: function(items) {
            if(!items)
                return;

            var existingIds = [];
            var lastId;
            for(var i = 0; i < items.length; i++) {
                if(items[i].id) {
                    existingIds.push(items[i].id);
                    if(!lastId)
                        lastId = items[i].id;
                    else
                        lastId = Math.max(lastId, items[i].id);
                }
            }
            for(var j = 0; j < items.length; j++) {
                if(!items[j].id) {
                    lastId++;
                    items[j].id = lastId;
                }
            }
            return items;
        },

        // Method that sort items, depending on their relativePosition
        _sortItemsPosition: function(items) {
            var hasRelativePositioning = hasNumberProperty(items, 'relativePosition');

            if(hasRelativePositioning) {
                items.sort(function(a, b) {
                    return a.relativePosition - b.relativePosition;
                });
            }
        },

        // Method that merges items with same relativePosition
        // !!! When 2 (or more) items have the same relativePosition, only the first item's type & picto will be taken into account !!!
        _mergeSamePositionItems: function(items) {
            var n = items.length - 1;
            for(var i = 0; i < n; i++) {
                if(items[i].relativePosition == items[i + 1].relativePosition) {
                    items[i].label = items[i].label + '<span class="timeline-me-same-position">' + items[i + 1].label + '</span>';
                    items[i].shortContent = items[i].shortContent + '<span class="timeline-me-same-position">' + items[i + 1].shortContent + '</span>';
                    items[i].fullContent = items[i].fullContent + '<span class="timeline-me-same-position">' + items[i + 1].fullContent + '</span>';
                    items.splice(i + 1, 1);
                    n--;
                }
            }
        },

        // Method that calculates relativePosition difference with next item
        _calcDiffWithNextItem: function(items) {
            var hasRelativePositioning = hasNumberProperty(items, 'relativePosition');

            for(var i = 0; i < items.length; i++) {
                if(i == items.length - 1)
                    items[i].diffWithNextRelativePos = undefined;
                else
                    items[i].diffWithNextRelativePos = items[i + 1].relativePosition - items[i].relativePosition;
            }
        },

        // Method that calculates ratio: item's height / relative pos diff with next item
        _calcRelativePosRatio: function(items) {
            for(var i = 0; i < items.length; i++) {
                var height = items[i].element[0].getBoundingClientRect().height;
                var relPosDiff = items[i].diffWithNextRelativePos;
                var ratio = 0;
                if(!isNaN(height) && !isNaN(relPosDiff))
                    ratio = height / relPosDiff;

                items[i].relativePosRatio = ratio;
            }
        },

        // Method that calculates items' "virtual" height depending on relative positioning
        _calcRelativeHeight: function(items, heightRatio) {
            if(!heightRatio) return;

            for(var i = 0; i < items.length; i++) {
                items[i].relativeHeight = heightRatio * (items[i].diffWithNextRelativePos ? items[i].diffWithNextRelativePos : 0);
            }
        },

        // Method that calculates items' "virtual" height depending on relative positioning
        _addMissingRelativeHeight: function(items) {
            for(var i = 0; i < items.length; i++) {
                var heightToAdd = 0;
                if(!isNaN(items[i].relativeHeight)) {
                    var height = items[i].element[0].getBoundingClientRect().height;
                    heightToAdd = items[i].relativeHeight - height;
                    var marginBottom = parseInt(items[i].element.css('margin-bottom')) + (heightToAdd > 0 ? heightToAdd : 0);
                    items[i].element.css('margin-bottom', marginBottom + 'px');
                }
            }
        },

        // Method that fill 'position' field, depending on item's forcePosition option, on item's type and on position of the previous item
        _fillItemsPosition: function(items) {
            if(!items)
                return;

            var positions;
            if(this.settings.orientation == 'horizontal')
                positions = ['top', 'bottom'];
            else
                positions = ['left', 'right'];

            for(var i = 0; i < this.content.length; i++) {
                if(this.content[i].forcePosition && positions.indexOf(this.content[i].forcePosition) >= 0) {
                        this.content[i].position = this.content[i].forcePosition;
                } else if(!this.content[i].position) {
                    switch(this.content[i].type) {
                        case 'milestone':
                            if(this.settings.orientation == 'horizontal')
                                this.content[i].position = 'top';
                            else
                                this.content[i].position = 'right';
                            break;
                        case 'smallItem':
                            if(i == 0)
                                this.content[i].position = this.settings.orientation == 'horizontal' ? 'top' : 'left';
                            else if(this.settings.orientation == 'horizontal' && this.content[i - 1].position == 'top')
                                this.content[i].position = 'bottom';
                            else if(this.settings.orientation == 'horizontal' && this.content[i - 1].position == 'bottom')
                                this.content[i].position = 'top';
                            else if(this.settings.orientation != 'horizontal' && this.content[i - 1].position == 'left')
                                this.content[i].position = 'right';
                            else if(this.settings.orientation != 'horizontal' && this.content[i - 1].position == 'right')
                                this.content[i].position = 'left';
                            else
                                this.content[i].position = this.settings.orientation == 'horizontal' ? 'top' : 'left';
                            break;
                        case 'bigItem':
                            break;
                    }
                }
            }

            return items;
        },

        // Method that refresh item's class depending on its 'position' property
        _refreshItemPosition: function(item) {
            if(!item || (item && !item.element) || (item && !item.position))
                return;

            switch(item.position) {
                case 'left':
                    item.element
                    .removeClass('timeline-me-top')
                    .removeClass('timeline-me-right')
                    .removeClass('timeline-me-bottom')
                    .addClass('timeline-me-left');
                    break;
                case 'top':
                    item.element
                    .removeClass('timeline-me-left')
                    .removeClass('timeline-me-right')
                    .removeClass('timeline-me-bottom')
                    .addClass('timeline-me-top');
                    break;
                case 'right':
                    item.element
                    .removeClass('timeline-me-top')
                    .removeClass('timeline-me-left')
                    .removeClass('timeline-me-bottom')
                    .addClass('timeline-me-right');
                    break;
                case 'bottom':
                    item.element
                    .removeClass('timeline-me-top')
                    .removeClass('timeline-me-right')
                    .removeClass('timeline-me-left')
                    .addClass('timeline-me-bottom');
                    break;
            }

            return item;
        },

        // Method that create the item's html structure and fill it
        _createItemElement: function(item) {
            var itemElm;
            switch(item.type) {
                case 'milestone':
                    itemElm = this._buildMilestoneElement(item);
                    break;
                case 'smallItem':
                    itemElm = this._buildSmallItemElement(item);
                    break;
                case 'bigItem':
                    itemElm = this._buildBigItemElement(item);
                    break;
            }
            item.element = itemElm;
            this._refreshItemPosition(item);

            item.element.on('timelineMe.itemHeightChanged', function(event) {
                // Do some stuff
            });
            item.element.on('timelineMe.smallItem.displayfull', function(event) {
                // Do some stuff
            });
            item.element.on('timelineMe.bigItem.flipped', function(event) {
                var container = item.element.find('.timeline-me-content-container');
                if(item.element.hasClass('timeline-me-flipped')) {
                    container.height(item.fullContentElement.outerHeight());
                } else {
                    container.height(item.shortContentElement.outerHeight());
                }
            });

            this._buildItemContent(item);
            this._fillItem(item);

            return itemElm;
        },

        // Method that build html element for corresponding item
        _buildMilestoneElement: function(item) {
            var milestoneElm = $('<div class="timeline-me-item timeline-me-milestone">');
            return milestoneElm;
        },

        // Method that build html element for corresponding item
        _buildSmallItemElement: function(item) {
            var smallItemElm = $('<div class="timeline-me-item timeline-me-smallitem">');
            return smallItemElm;
        },

        // Method that build html element for corresponding item
        _buildBigItemElement: function(item) {
            var bigItemElm = $('<div class="timeline-me-item timeline-me-bigitem">');
            return bigItemElm;
        },

        // Method that fills the item's element with some useful html structure
        _buildItemContent: function(item) {
            if(!item || !item.element)
                return;

            var pixelsRegex = /[0-9]+px$/;
            // Following wrapper are only used in horizontal mode, in order to correctly display bigItems (with table display)
            var itemWrapper = $('<div class="timeline-me-item-wrapper">');
            var labelWrapper = $('<div class="timeline-me-label-wrapper">');
            var contentWrapper = $('<div class="timeline-me-content-wrapper">');

            var labelElm = $('<div class="timeline-me-label">');
            item.labelElement = labelElm;
            if(this.settings.orientation == 'horizontal' && this.settings.labelDimensionValue && pixelsRegex.test(this.settings.labelDimensionValue)) {
                labelElm.css('width', this.settings.labelDimensionValue);
            }
            var pictoElm = $('<div class="timeline-me-picto">');
            item.pictoElement = pictoElm;

            labelWrapper.append(labelElm);
            itemWrapper.append(labelWrapper);

            if(item.type == 'smallItem' || item.type == 'bigItem') {
                var contentContainer = $('<div class="timeline-me-content-container">');
                var contentElm = $('<div class="timeline-me-content"></div>');
                contentContainer.append(contentElm);
                if(this.settings.orientation == 'horizontal' && this.settings.contentDimensionValue && pixelsRegex.test(this.settings.contentDimensionValue)) {
                    contentElm.css('width', this.settings.contentDimensionValue);
                }

                var shortContentElm = $('<div class="timeline-me-shortcontent">');
                contentElm.append(shortContentElm);
                item.shortContentElement = shortContentElm;

                var fullContentElm = $('<div class="timeline-me-fullcontent">');
                contentElm.append(fullContentElm);
                item.fullContentElement = fullContentElm;

                var showMoreElm = $('<div class="timeline-me-showmore">');
                item.showMoreElement = showMoreElm;

                var showLessElm = $('<div class="timeline-me-showless">');
                item.showLessElement = showLessElm;

                contentWrapper.append(contentContainer);
                itemWrapper.append(contentWrapper);
            }

            item.element.append(itemWrapper);

            item.itemWrapperElement = itemWrapper;
            item.labelWrapperElement = labelWrapper;
            item.contentWrapperElement = contentWrapper;
        },

        // Method that fills the item's element with content passed through the options
        _fillItem: function(item) {
            if((item.label || item.labelTemplate) && item.labelElement) {
                item.label ? item.labelElement.html(item.label) : item.labelElement.html(loadHTMLFromFile(item.labelTemplate));
                item.labelElement.append(item.pictoElement);
            }
            if(this.settings.labelClass && item.labelElement) {
                item.labelElement.addClass(this.settings.labelClass);
            }
            if(item.picto || item.pictoTemplate) {
                item.picto ? item.pictoElement.html(item.picto) : item.pictoElement.html(loadHTMLFromFile(item.pictoTemplate));
            }

            if((item.shortContent || item.shortContentTemplate) && item.shortContentElement) {
                item.shortContent ? item.shortContentElement.html(item.shortContent) : item.shortContentElement.html(loadHTMLFromFile(item.shortContentTemplate));
                /*
                // Two solutions to get element's height:
                resolveElementHeight(item.shortContentElement).then(function(data) {
                    console.log('resolved');
                    console.log(data);
                });
                eventElementHeight(item.shortContentElement, {eventName: 'timelineMe.heightChanged'});
                */

                if(item.type == 'bigItem') {
                    // Use height watcher:
                    heightWatcher(item.shortContentElement, function(data) {
                        if(data && data.element) {
                            var container = data.element.closest('.timeline-me-content-container');
                            if(!data.oldVal && data.newVal && !data.element.hasClass('timeline-me-displayfull'))
                                container.height(data.newVal);
                        }
                    });
                    heightWatcher(item.fullContentElement, function(data) {
                        if(data && data.element) {
                            var container = data.element.closest('.timeline-me-content-container');
                            if(data.newVal && !data.oldVal && data.element.hasClass('timeline-me-displayfull'))
                                container.height(data.newVal);
                        }
                    });
                }
            }
            if(this.settings.shortContentClass && item.shortContentElement) {
                item.shortContentElement.addClass(this.settings.shortContentClass);
            }

            if((item.fullContent || item.fullContentTemplate) && item.fullContentElement) {
                item.fullContent ? item.fullContentElement.html(item.fullContent) : item.fullContentElement.html(loadHTMLFromFile(item.fullContentTemplate));
            }
            if(this.settings.fullContentClass && item.fullContentElement) {
                item.fullContentElement.addClass(this.settings.fullContentClass);
            }

            if((item.showMore || item.showMoreTemplate) && item.showMoreElement) {
                item.showMore ? item.showMoreElement.html(item.showMore) : item.showMoreElement.html(loadHTMLFromFile(item.showMoreTemplate));
                if(item.shortContentElement)
                    item.shortContentElement.append(item.showMoreElement);
                if(item.type == 'smallItem') {
                    item.showMoreElement.on('click', function() {
                        item.element.toggleClass('timeline-me-displayfull');
                        item.element.trigger('timelineMe.smallItem.displayfull');
                    });
                } else if(item.type == 'bigItem') {
                    item.showMoreElement.on('click', function() {
                        item.element.toggleClass('timeline-me-flipped');
                        item.element.trigger('timelineMe.bigItem.flipped');
                    });
                }
            }
            if(this.settings.showMoreClass && item.showMoreElement) {
                item.showMoreElement.addClass(this.settings.showMoreClass);
            }

            if((item.showLess || item.showLessTemplate) && item.showLessElement) {
                item.showLess ? item.showLessElement.html(item.showLess) : item.showLessElement.html(loadHTMLFromFile(item.showLessTemplate));
                if(item.fullContentElement)
                    item.fullContentElement.append(item.showLessElement);
                if(item.type == 'smallItem') {
                    item.showLessElement.on('click', function() {
                        item.element.toggleClass('timeline-me-displayfull');
                        item.element.trigger('timelineMe.smallItem.displayfull');
                    });
                } else if(item.type == 'bigItem') {
                    item.showLessElement.on('click', function() {
                        item.element.toggleClass('timeline-me-flipped');
                        item.element.trigger('timelineMe.bigItem.flipped');
                    });
                }
            }
            if(this.settings.showLessClass && item.showLessElement) {
                item.showLessElement.addClass(this.settings.showLessClass);
            }

            // if the timeline is in horizontal mode, we create a cloned version of the contentWrapper element, that will be hidden,
            // in order to place it correctly above/below the timeline (will simulate a margin-bottom/margin-top equals to the height of the contentWrapper)
            if(item.type == 'smallItem' && this.settings.orientation == 'horizontal') {
                if(item.contentWrapperClone)
                    delete item.contentWrapperClone;

                var contentWrapperClone = item.contentWrapperElement.clone();
                if(item.position == 'top')
                    item.contentWrapperElement.after(contentWrapperClone);
                if(item.position == 'bottom')
                    item.contentWrapperElement.before(contentWrapperClone);
                contentWrapperClone.addClass('timeline-me-hidden');
                item.contentWrapperClone = contentWrapperClone;
            }
        },

        // Method that will refresh items' elements (still in progress)
        _refreshItems: function() {
            for(var i = 0; i < this.content.length; i++) {
                if(!this.content[i].element || !this._isItemClassCorrespondingToType(this.content[i])) {
                    this._createItemElement(this.content[i])
                }
                this._fillItem(this.content[i]);
            }
        },

        // Method that checks if an item's element has the class defined by his type
        _isItemClassCorrespondingToType: function(item) {
            if(!item || !item.type || !item.element)
                return false;

            switch(item.type) {
                case 'milestone':
                    return (item.element.hasClass('timeline-milestone'));
                    break;
                case 'smallItem':
                    return (item.element.hasClass('timeline-smallitem'));
                    break;
                case 'bigItem':
                    return (item.element.hasClass('timeline-bigitem'));
                    break;
                default:
                    return false;
                    break;
            }
        }
    };

    /**
     * These are real private methods. A plugin instance has access to them
     * @return {[type]}
     */

    // Method that bind left & right scroll to any scrollable element
    var bindScrollTo = function(leftScrollElm, rightScrollElm, elmToScroll, scrollSpeed) {
        var scrollSpeed = scrollSpeed ? scrollSpeed : 5;

        leftScrollElm.on('mouseenter', function() {
            var timer = setInterval(function() {
                elmToScroll.scrollLeft(elmToScroll.scrollLeft() - scrollSpeed);
            }, 20);

            leftScrollElm.on('mouseleave', function() {
                clearInterval(timer);
            });
        });
        rightScrollElm.on('mouseenter', function() {
            var timer = setInterval(function() {
                elmToScroll.scrollLeft(elmToScroll.scrollLeft() + scrollSpeed);
            }, 20);

            rightScrollElm.on('mouseleave', function() {
                clearInterval(timer);
            });
        });
    }

    // Method that return container width depending on children's width
    var resolveContainerWidth = function(element) {
        if(!element) return;

        var children = element.children();
        if(children.length <= 0) return;

        var totalWidth = 0;
        for(var i = 0; i < children.length; i++) {
            totalWidth += $(children[i]).width();
        }

        element.width(totalWidth);
    };

    // Method that can return an element's height through a promise (so it'll be resolve only when the element will have a positive height)
    var resolveElementHeight = function(element, args) {
        if(!args)
            args = {};
        var refreshDelay = args.refreshDelay ? args.refreshDelay : 500;
        var ret = new $.Deferred();
        var elmHeight;

        if(element)
            elmHeight = element.height();
        if(elmHeight && elmHeight > 0) {
            ret.resolve(elmHeight);
        } else {
            setTimeout(function () {
                resolveElementHeight(element, args).then(function(heightData) {
                    ret.resolve(heightData);
                });
            }, refreshDelay);
        }

        return ret;
    }

    // Method that can return an element's height through an event (event will be fired only when the element will have a positive height)
    var eventElementHeight = function(element, args) {
        if(!args)
            args = {};
        var eventName = args.eventName ? args.eventName : 'onElementHeight';
        var refreshDelay = args.refreshDelay ? args.refreshDelay : 500;
        var elmHeight;

        if(element)
            elmHeight = element.height();
        if(elmHeight && elmHeight > 0) {
            var e = jQuery.Event(eventName, {elementHeight: elmHeight});
            element.trigger(e);
        } else {
            setTimeout(function () {
                eventElementHeight(element, args);
            }, refreshDelay);
        }
    }

    // Method that can return an element's height when it's changing
    var resolveElementHeightChange = function(element, args) {
        if(!args)
            args = {};
        var refreshDelay = args.refreshDelay ? args.refreshDelay : 500;
        var previousHeight = args.previousHeight;
        var level = args.level ? args.level : 0;
        var ret = new $.Deferred();
        var elmHeight;

        if(element)
            elmHeight = element[0].getBoundingClientRect().height;

        if(elmHeight && (!previousHeight || previousHeight != elmHeight)) {
            ret.resolve(elmHeight, previousHeight, level);
        } else {
            args.previousHeight = elmHeight;
            setTimeout(function () {
                resolveElementHeightChange(element, {previousHeight: elmHeight, refreshDelay: refreshDelay, level: (level + 1)}).then(function(newHeightVal, previousHeightVal, levelVal) {
                    ret.resolve(newHeightVal, previousHeightVal, level);
                });
            }, refreshDelay);
        }

        return ret;
    }

    // Method that can watch for an element's height change (uses the resolveElementHeightChange function)
    var heightWatcher = function(element, callback) {
        resolveElementHeightChange(element).then(function(newVal, oldVal) {
            var heightEvent = jQuery.Event('timelineMe.itemHeightChanged', {elementHeight: newVal, previousHeight: oldVal});
            element.trigger(heightEvent);
        });
        element.on('timelineMe.itemHeightChanged', function(e) {
            callback({element: element, newVal: e.elementHeight, oldVal: e.previousHeight});
            resolveElementHeightChange($(e.target), {previousHeight: e.elementHeight}).then(function(newVal, oldVal) {
                var heightEvent = jQuery.Event('timelineMe.itemHeightChanged', {elementHeight: newVal, previousHeight: oldVal});
                $(e.target).trigger(heightEvent);
            });
        });
    }

    // Method checking if all items of an array have a specific number property
    var hasNumberProperty = function(items, propertyName) {
        if(!items) return false;

        var hasProperty = true;
        for(var i = 0; i < items.length; i++) {
            if(isNaN(items[i][propertyName]))
                hasProperty = false;
        }
        return hasProperty;
    }

    // Method returning max value of specific array's property
    var getMaxPropertyValue = function(items, propertyName) {
        if(!items) return false;

        var maxProperty;
        for(var i = 0; i < items.length; i++) {
            if(maxProperty == undefined)
                maxProperty = items[i][propertyName];
            else if(items[i][propertyName] > maxProperty)
                maxProperty = items[i][propertyName];
        }
        return maxProperty;
    }

    //
    var loadHTMLFromFile = function(url) {
        var html;
        $.ajax({
            url: url,
            success: function (result) {
                html = result;
            },
            async: false
        });
        return html;
    }

    $.fn[pluginName] = function(options) {
        var args = arguments;

        if (options === undefined || typeof options === 'object') {
            // Creates a new plugin instance, for each selected element, and
            // stores a reference withint the element's data
            return this.each(function() {
                if (!$.data(this, 'plugin_' + pluginName)) {
                    $.data(this, 'plugin_' + pluginName, new TimelineMe(this, options));
                }
            });
        } else if (typeof options === 'string' && options[0] !== '_' && options !== 'init') {
            // Call a public plugin method (not starting with an underscore) for each
            // selected element.
            if ($.inArray(options, $.fn[pluginName].getters) !== -1) {
                // If the user does not pass any arguments and the method allows to
                // work as a getter then break the chainability so we can return a value
                // instead the element reference.
                var instance = $.data(this[0], 'plugin_' + pluginName);
                return instance[options].apply(instance, Array.prototype.slice.call(args, 1));
            } else {
                // Invoke the speficied method on each selected element
                return this.each(function() {
                    var instance = $.data(this, 'plugin_' + pluginName);
                    if (instance instanceof TimelineMe && typeof instance[options] === 'function') {
                        instance[options].apply(instance, Array.prototype.slice.call(args, 1));
                    }
                });
            }
        }
    };

    /**
     * Names of the pluguin methods that can act as a getter method.
     * @type {Array}
     */
    $.fn[pluginName].getters = ['getItem', 'getItemIndex'];

    /**
     * Default options
     */
    $.fn[pluginName].defaults = {
        orientation             : 'vertical',
        items                   : [],
        // horizontal-orientation specific options
        contentDimensionValue   : '400px',
        labelDimensionValue     : '200px',
        scrollBar               : true,
        scrollZones             : false,
        scrollArrows            : false,
        leftArrowElm            : undefined,
        rightArrowElm           : undefined
    };

}(jQuery));
