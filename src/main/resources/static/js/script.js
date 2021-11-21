console.log("this is script file");

const toggleSidebar = () => {
    if($('.sidebar').is(":visible")) {
        // true (band krna hai)
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
    } else {
        // flase (show krna hai)
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
};

