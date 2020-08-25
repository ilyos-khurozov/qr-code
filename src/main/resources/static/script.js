function getQR() {
    let txt = $("#text").val();
    if (txt === "") {
        alert("Enter text !");
        return;
    }

    $.ajax({
        url: "/qr-create",
        data: {txt: txt},
        type: "GET",
        success: function (qr) {
            $("#created-img").attr("src", "data:image/png;base64," + qr);
        }
    })
}

function scanQR() {
    let multi = $("#file")[0].files[0];
    let area = $("#scanned-text");
    let formData = new FormData();
    formData.append("multi", multi);

    $.ajax({
        url: "/qr-scan",
        data: formData,
        enctype: "multipart/form-data",
        type: "POST",
        contentType: false,
        processData: false,
        cache: false,
        success: function (jsonStr) {
            let data = JSON.parse(jsonStr);

            if (data.status === "error"){
                $(area).addClass("d-none");
                alert("Enter QR-Code image !");
                return;
            }

            $(area).html(data.txt);
            $(area).removeClass("d-none");
        }
    })
}

function change(input) {
    let preview = $("#preview");
    let btn = $("#btn-scan");
    let area = $("#scanned-text");

    if (input.files && input.files[0]){
        let fileName = input.files[0].name;


        if (fileName.substr(fileName.lastIndexOf('.')+1) !== "png"){
            $(input).siblings(".custom-file-label").removeClass("selected").html("Choose file");
            $(btn).addClass("d-none");
            $(area).addClass("d-none");
            $(preview).removeAttr("src");
            alert("Select PNG type image !");
            return;
        }

        $(input).siblings(".custom-file-label").addClass("selected").html(fileName);
        let reader = new FileReader();

        reader.onload = function(){
            $(preview).attr("src", reader.result);
        }

        reader.readAsDataURL(input.files[0]);
        $(btn).removeClass("d-none");
    } else {
        $(input).siblings(".custom-file-label").removeClass("selected").html("Choose file");
        $(btn).addClass("d-none");
        $(area).addClass("d-none");
        $(preview).removeAttr("src");
    }
}