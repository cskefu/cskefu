
$(document).ready(function () {
    /*  uk-nav-parent-icon */
    $(".menu-begin ul:first-child").addClass('uk-nav uk-nav-side');
    $(".menu-begin ul:first-child").data('uk-nav', '{multiple:false}');
    $(".menu-begin ul:first-child").attr('data-uk-nav', '{multiple:false}');
 
    // Parent click
    $(".uk-parent > a").on('click', function (event) {
        
        event.preventDefault();
        $(".uk-parent > a").parent().removeClass('uk-active');
        $(this).parent().toggleClass('uk-active');

        var hash = $(this).attr('href').split('#')[1];

        // scroll.To(hash);
        scrollTo(hash);
        // $('body').scrollTo(hash);
        // $('body').scrollTo(hash,{duration:'slow', offsetTop : '50'});
        $('a[href="' + hash + '"]').addClass('uk-active');
    });
    
});

function scrollTo(hash) {
    
    location.hash = "#" + hash;
    
}

function setStickyMenu () {
     
    var options = {
        bottoming: false,
        inner_scrolling: false
    };
    
    var sticky = ".uk-overflow-container";
    
    
    
    
    
    
    var width = $(window).width();
    
    if (width >= 768) {
        
        $(".uk-nav > li").addClass('uk-parent');
        $(".menu-begin ul > li > ul").addClass('uk-nav-sub');
        
        $('.uk-parent').removeClass('uk-open');
        $('.uk-parent div').addClass('uk-hidden');

        $(sticky).stick_in_parent(options);

    } else {

        
        $('.uk-parent').addClass('uk-open');
        
        $('.uk-parent div').removeClass('uk-hidden');
        $('.uk-parent div').removeAttr('style');
        $('.uk-nav li').removeClass('uk-parent');
        
        
        $(sticky).trigger("sticky_kit:detach"); 
    }
};

$(document).ready(function(){
    
            $(".pager").wrapInner( '<ul class="uk-pagination-left uk-pagination"></ul>' );        
            $(".pager a").wrap( '<li></li>' );
            $(".pager b").wrap( '<li class="uk-active"></li>' );
            $(".pager b").wrapInner( '<span></span>' );
            $(".pager b span").unwrap( );

            // Normalize tables without style
            $( "table" ).addClass( "uk-table" );

            // Normalize tables without style
            $( "form" ).addClass( "uk-form" );
        
        
  
});

$(document).ready(function () {
    setStickyMenu();
    $(window).on('resize', function () {
        setStickyMenu();
    });
});


$(document).ready(function () {
    $( 'table' ).addClass( "table" );
});

/*
var scroll = (function() {

    var elementPosition = function(a) {
        return function() {
            return a.getBoundingClientRect().top;
        };
    };

    var scrolling = function( elementID ) {

        var el = document.getElementById( elementID ),
            elPos = elementPosition( el ),
            duration = 400,
            increment = Math.round( Math.abs( elPos() )/40 ),
            time = Math.round( duration/increment ),
            prev = 0,
            E;

        function scroller() {
            E = elPos();

            if (E === prev) {
                return;
            } else {
                prev = E;
            }

            increment = (E > -20 && E < 20) ? ((E > - 5 && E < 5) ? 1 : 5) : increment;

            if (E > 1 || E < -1) {

                if (E < 0) {
                    window.scrollBy( 0,-increment );
                } else {
                    window.scrollBy( 0,increment );
                }

                setTimeout(scroller, time);

            } else {

                el.scrollTo( 0,0 );

            }
        }

        scroller();
    };

    return {
        To: scrolling
    }

})();
*/