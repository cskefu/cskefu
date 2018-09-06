/*
* Timeliner.js
* @version      2.31
* @copyright    Tarek Anandan (http://www.technotarek.com)
*/
;(function($) {

        $.timeliner = function(options) {
            if ($.timeliners == null) {
                $.timeliners = { options: [] };
                $.timeliners.options.push(options);
            }
            else {
                $.timeliners.options.push(options);
            }
            $(document).ready(function() {
                for (var i=0; i<$.timeliners.options.length; i++) {
                    startTimeliner($.timeliners.options[i]);
                }
            });
        }

        function startTimeliner(options) {
            var settings = {
                timelineContainer: options['timelineContainer'] || '#timeline',
                // Container for the element holding the entire timeline (e.g. a DIV)
                // value: ID or class selector
                // default: #timeline
                // note: must be unique for each timeline on page

                timelineSection: options['timelineSection'] || '.timeline-wrapper',
                // Wrapper that surrounds a time and series of events (e.g., all of the events under a year on the timeline)
                // value: class selector
                // default: .timeline-wrapper
                // note: changing this selector from the default will require modifications to the CSS file in order to retain default styling

                timelineSectionMarker: options['timelineSectionMarker'] || '.timeline-time',
                // Class selector applied to each major item on the timeline, such as each year
                // value: class selector
                // default: .timeline-time

                timelineTriggerContainer: options['timelineTriggerContainer'] || '.timeline-series',
                // Wrapper surrounding a series of events corresponding to the timelineSectionMarker
                // value: selector
                // default: .timeline-series
                // note: changing this selector from the default will require modifications to the CSS file in order to retain default styling

                timelineTriggerAnchor: options['timelineTriggerAnchor'] || '.timeline-event',
                // Element that is wrapped around the event's title; when clicked, expands the event and reveals its full contents
                // value: class
                // default: .timeline-event
                // note: changing this tag from the default will require modifications to the CSS file in order to retain default styling

                timelineEventContainer: options['timelineEventContainer'] || 'dt',
                // Wrapper surrounding a series of events corresponding to the timelineSectionMarker
                // value: tag or class selector
                // default: dt
                // note: When leaving this value at its default, you do not need to apply a class to the dt element if you use the plugins recommended tag structure and markup
                // note: Change this from the default, perhaps to a class like ".timeline-event", in the case that you do not want to use the plugins recommened markup structure and prefer to use anothe element (e.g, div) instead of a dt tag to mark each event within a series.
                // note: Changing this value from the default will require modifications to the CSS file in order to retain default styling

                timelineEXContent: options['timelineEXContent'] || '.timeline-event-content',
                // Element that contains the event's full content to be displayed when event is expanded, an event's expanded ID should alway be on this item
                // value: class selector
                // default: .timeline-event
                // note: changing this selector from the default will require modifications to the CSS file in order to retain default styling

                EXContentIdSuffix: options['timelineEXContentSuffix'] || 'EX',
                // ID suffix to identify expanded (aka EX) content
                // value: string
                // default: EX

                oneOpen: options['oneOpen'] || false,
                // sets whether only one item on the timeline can be open at a time. If true, other items will close when one is opened.
                // value: true | false
                // default: false
                // note: does not apply to events identified in startOpen option

                startState: options['startState'] || 'closed',
                // sets whether the timeline is initially collapsed, fully expanded, or "flat" mode
                // value: closed | open | flat
                // default: closed
                // note: setting to "open" makes the startOpen option meaningless
                // note: flat mode initally collapses the entire timeline except for the major markers
                // note: the flat state is an initial display option only -- the timeline major markers return to normal once they've been opened/displayed

                startOpen: options['startOpen'] || ['.start-open'],
                // As of version 2.3, you can simply add the "start-open" class to each timeline-event you want to have open by default; see the demo source code or code sample below
                // sets the events to display expanded on page load
                // value: array of IDs of single timelineEvents (e.g., ['#event01'] or ['#event01','#event02'])
                // default: ['.start-open']

                baseSpeed: options['baseSpeed'] || 200,
                // sets the base speed for animation of an event
                // value: numeric
                // default: 200

                speed: options['speed'] || 4,
                // multiplier applied to the base speed that sets the speed at which an event's contents are displayed and hidden
                // value: numeric
                // default: 4

                fontOpen: options['fontOpen'] || '1.2em',
                // sets the font size of an event after it is opened
                // value: any valid CSS font-size value,
                // default: 1.2em

                fontClosed: options['fontClosed'] || '1em',
                // sets the font size of an event after it is closed
                // value: any valid CSS font-size value
                // defaults: 1em

                expandAllText: options ['expandAllText'] || '+ expand all',
                // sets the text of the expandAll selector after the timeline is fully collapsed
                // value: string
                // default: + expand all

                collapseAllText: options['collapseAllText'] || '- collapse all'
                //sets the text of the expandAll selector after the timeline is fully expanded
                // value: string
                // default: - collapse all
            };

            function openStartEvents(events) {
                // show startOpen events
                $.each(events, function(index, value) {

                    // first make sure all events in the series are visible (overriding flat mode), then show individual events per option settings
                    $(value).parents(settings.timelineTriggerContainer).show(settings.speed*settings.baseSpeed, function(){
                        openEvent($(value),$(value).next(settings.timelineEXContent));
                    });

                });
            }

            function openEvent(eventHeading,eventBody) {

                if(settings.startState==='flat'){
                    // if flat mode, make sure parent series is visible
                    $(eventHeading).parents(settings.timelineTriggerContainer).show();
                }

                $(eventHeading).find('a')
                    .removeClass('closed')
                    .addClass('open')
                    .animate({ fontSize: settings.fontOpen }, settings.baseSpeed);
                $(eventBody).show(settings.speed*settings.baseSpeed);

            }

            function closeEvent(eventHeading,eventBody) {

                $(eventHeading).find('a')
                    .animate({ fontSize: settings.fontClosed }, 0)
                    .removeClass('open')
                    .addClass('closed');
                $(eventBody).hide(settings.speed*settings.baseSpeed);
            }

            if ($(settings.timelineContainer).data('started')) {
                return;
                // we already initialized this timelineContainer
            } else {
                $(settings.timelineContainer).data('started', true);
                $(settings.timelineContainer+" "+".timeline-toggle").html(settings.expandAllText);
                $(settings.timelineContainer+" "+".collapseAll").html(settings.collapseAllText);

                if(settings.startState==='flat')
                {
                    // hide all series (event headings)
                    $(settings.timelineContainer+' '+settings.timelineTriggerContainer).hide();

                    openStartEvents($(settings.startOpen));

                }else if(settings.startState==='closed')
                {
                    // If startState option is set to closed, hide all the event contents; else, show fully expanded upon load

                    // Close all items
                    $(settings.timelineContainer+" "+settings.timelineEXContent).hide();

                    openStartEvents($(settings.startOpen));

                }else{

                    // Open all items
                    openEvent($(settings.timelineContainer+" "+settings.timelineTriggerContainer+" "+settings.timelineTriggerAnchor),$(settings.timelineContainer+" "+settings.timelineEXContent));

                }

                // Minor Event Click
                $(settings.timelineContainer).on("click",settings.timelineTriggerContainer+" "+settings.timelineEventContainer,function(){

                    var currentId = $(this).attr('id');

                    // if the event is currently open
                    if($(this).find('a').is('.open'))
                    {
                        closeEvent($(this),$("#"+currentId+settings.EXContentIdSuffix))

                    } else{
                    // if the event is currently closed

                        if( settings.oneOpen == true ) {
                            closeEvent($(this).parents(settings.timelineContainer).find(settings.timelineTriggerAnchor,settings.timelineTriggerContainer),$(this).parents(settings.timelineContainer).find(settings.timelineEXContent));
                        }

                        openEvent($(this),$("#"+currentId+settings.EXContentIdSuffix));

                    }

                });

                // Major Marker Click
                // Overrides the 'oneOpen' option
                $(settings.timelineContainer).on("click",settings.timelineSectionMarker,function()
                {

                    // number of minor events under this major event
                    var numEvents = $(this).parents(settings.timelineSection).find(settings.timelineTriggerContainer).length;

                    // number of minor events already open
                    var numOpen = $(this).parents(settings.timelineSection).find('.open').length;

                    // This closes other items if oneOpen is true. It looks odd if an item in the section was open. Need to improve this.
                    if( settings.oneOpen == true ) {
                        closeEvent($(this).parents(settings.timelineContainer).find(settings.timelineTriggerAnchor,settings.timelineTriggerContainer),$(this).parents(settings.timelineContainer).find(settings.timelineEXContent));
                    }

                    if(numEvents > numOpen)
                    {
                        // if there are more events available than are displayed, then fully expand all events below MajorMarker
                        openEvent($(this).parents(settings.timelineSection).find(settings.timelineTriggerAnchor,settings.timelineTriggerContainer),$(this).parents(settings.timelineSection).find(settings.timelineEXContent));
                    }else
                    {
                        closeEvent($(this).parents(settings.timelineSection).find(settings.timelineTriggerContainer),$(this).parents(settings.timelineSection).find(settings.timelineEXContent));
                    }
                });

                // All Markers/Events
                var el = settings.timelineContainer+" "+".timeline-toggle";
                $(el).click(function()
                {
                    if($(el).hasClass('expanded'))
                    {

                        closeEvent($(el).parents(settings.timelineContainer).find(settings.timelineTriggerAnchor,settings.timelineTriggerContainer),$(el).parents(settings.timelineContainer).find(settings.timelineEXContent));
                        $(el).removeClass('expanded').html(settings.expandAllText);

                    } else{

                        openEvent($(el).parents(settings.timelineContainer).find(settings.timelineTriggerAnchor,settings.timelineTriggerContainer),$(el).parents(settings.timelineContainer).find(settings.timelineEXContent));
                        $(el).addClass('expanded').html(settings.collapseAllText);


                    }
                });
            }
        };

})(jQuery);
