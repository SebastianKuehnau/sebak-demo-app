window.Vaadin.Flow.stockdataGridConnector = {

    init: function () {
        this.ids = [];
        this.CSS_CLASS_NAME = "updatable";
    },

    removeUpdatableStyleClass: function () {
        //console.log("remove class from elements " + this.ids.length + " (" + this.ids + ")");
        for (let i = 0; i < this.ids.length; i++) {
            let element = this.getElement(this.ids[i]);

            if (element != null)
                element.classList.remove(this.CSS_CLASS_NAME);
        }
        this.ids = [];
    },

    addUpdatableStyleClasses: function (idString) {
        let ids = idString.split(",");
        //console.log("add class to elements " + ids.length + " (" + ids + ")");

        for (let i = 0; i < ids.length; i++)
            this.addUpdatableStyleClass(ids[i]);
    },

    addUpdatableStyleClass: function (id) {
        if (id.length > 0) {
            //console.log("add class to css " + id);
            let element = this.getElement(id);

            if (element != null)
                element.classList.add(this.CSS_CLASS_NAME);

            this.ids.push(id);
        }
    },

    getElement: function (id) {
        return document.getElementById("element-" + id);
    }
}