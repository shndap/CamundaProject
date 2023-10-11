function getJson() {
    const children = document.getElementById('dynamic-fields').children;

    var json = "[\n";
    var i = 0;

    function addToJson() {
        if (i % 4 === 0) {
            if (i !== 0) {
                json += ",\n";
            }
            json += "{\ntype: " + child.value + ",\n";
        } else if (i % 4 === 1) {
            json += "value: " + child.value + ",\n";
        } else if (i % 4 === 2) {
            json += "name: " + child.value + "\n}"
        }
        i += 1;
    }

    for (var child of children) {
        addToJson();
    }

    json += "\n]";

    return json
}

function updateText() {
    document.getElementById('jsonText').value = getJson();
}

document.getElementById('save-button').addEventListener('click', function () {
    document.getElementById('stat').innerText = 'Saved at ' + new Date().toTimeString();
    updateText();
});

document.getElementById('add-button').addEventListener('click', function () {
    var dynamicFields = document.getElementById('dynamic-fields');

    // Create a new pair of input fields
    var newKeyField = document.createElement('input');
    var num = Math.ceil(dynamicFields.children.length / 3);

    newKeyField.type = 'text';
    newKeyField.className = 'form-control';
    newKeyField.name = 'key' + num;
    newKeyField.placeholder = 'type_' + num;

    var newValueField = document.createElement('input');
    newValueField.type = 'text';
    newValueField.className = 'form-control';
    newValueField.name = 'value' + num;
    newValueField.placeholder = 'value_' + num;

    var newNameField = document.createElement('input');
    newNameField.type = 'text';
    newNameField.className = 'form-control';
    newNameField.name = 'name' + num;
    newNameField.placeholder = 'name_' + num;

    // Append the new input fields to the dynamic fields div
    dynamicFields.appendChild(newKeyField);
    dynamicFields.appendChild(newValueField);
    dynamicFields.appendChild(newNameField);
    dynamicFields.appendChild(document.createElement('br'));
});