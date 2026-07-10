<%@ include file="header.jspf" %>
<h1>Create a Quiz</h1>

<form method="post" action="${pageContext.request.contextPath}/createQuiz" id="quizForm" class="card">
    <label>Title
        <input type="text" name="title" required/>
    </label>
    <label>Description
        <textarea name="description"></textarea>
    </label>

    <label class="option"><input type="checkbox" name="randomize"/> Randomize question order</label>
    <label class="option"><input type="checkbox" name="multiPage" id="multiPage"/> One question per page</label>
    <label class="option"><input type="checkbox" name="immediateCorrect"/> Immediate correction (multi-page only)</label>
    <label class="option"><input type="checkbox" name="practiceAllowed"/> Allow practice mode</label>

    <div id="questions"></div>
    <input type="hidden" name="questionCount" id="questionCount" value="0"/>

    <button type="button" class="btn" onclick="addQuestion()">Add Question</button>
    <button type="submit" class="btn primary">Create Quiz</button>
</form>

<script>
    var count = 0;

    function addQuestion() {
        var i = count++;
        document.getElementById('questionCount').value = count;

        var div = document.createElement('div');
        div.className = 'question card';
        div.innerHTML =
            '<h3>Question ' + (i + 1) + '</h3>' +
            '<label>Type ' +
            '<select name="type' + i + '" onchange="updateFields(' + i + ')" id="type' + i + '">' +
            '<option value="QUESTION_RESPONSE">Question-Response</option>' +
            '<option value="FILL_BLANK">Fill in the Blank</option>' +
            '<option value="MULTIPLE_CHOICE">Multiple Choice</option>' +
            '<option value="PICTURE_RESPONSE">Picture-Response</option>' +
            '</select></label>' +
            '<label>Question text ' +
            '<input type="text" name="text' + i + '" required/></label>' +
            '<div id="imageWrap' + i + '" style="display:none">' +
            '<label>Image URL <input type="text" name="image' + i + '" class="dyn"/></label>' +
            '</div>' +
            '<div id="answersWrap' + i + '">' +
            '<label>Accepted answer <input type="text" name="answer' + i + '" class="dyn"/></label>' +
            '<div id="extraAnswers' + i + '"></div>' +
            '<button type="button" class="btn small" onclick="addAnswer(' + i + ')">Add another accepted answer</button>' +
            '</div>' +
            '<div id="optionsWrap' + i + '" style="display:none">' +
            '<div id="optionRows' + i + '"></div>' +
            '<button type="button" class="btn small" onclick="addOption(' + i + ')">Add option</button>' +
            '</div>';
        document.getElementById('questions').appendChild(div);
        addOption(i);
        addOption(i);
    }

    function addAnswer(i) {
        var row = document.createElement('label');
        row.innerHTML = 'Accepted answer <input type="text" name="answer' + i + '" class="dyn"/>';
        document.getElementById('extraAnswers' + i).appendChild(row);
    }

    function addOption(i) {
        var rows = document.getElementById('optionRows' + i);
        var j = rows.children.length;
        var row = document.createElement('label');
        row.className = 'option';
        row.innerHTML =
            '<input type="radio" name="correct' + i + '" value="' + j + '"' + (j === 0 ? ' checked' : '') + '/> ' +
            'Option <input type="text" name="option' + i + '" class="dyn"/>';
        rows.appendChild(row);
    }

    function updateFields(i) {
        var type = document.getElementById('type' + i).value;
        document.getElementById('imageWrap' + i).style.display =
            type === 'PICTURE_RESPONSE' ? '' : 'none';
        document.getElementById('optionsWrap' + i).style.display =
            type === 'MULTIPLE_CHOICE' ? '' : 'none';
        document.getElementById('answersWrap' + i).style.display =
            type === 'MULTIPLE_CHOICE' ? 'none' : '';
    }

    document.getElementById('quizForm').addEventListener('submit', function (e) {
        if (count === 0) {
            e.preventDefault();
            alert('Add at least one question.');
            return;
        }
        for (var i = 0; i < count; i++) {
            var rows = document.getElementById('optionRows' + i);
            if (!rows) continue;
            var compact = 0;
            for (var j = 0; j < rows.children.length; j++) {
                var radio = rows.children[j].querySelector('input[type=radio]');
                var text = rows.children[j].querySelector('input[type=text]');
                if (text.value.trim()) {
                    radio.value = compact++;
                } else {
                    radio.disabled = true;
                }
            }
        }
        var inputs = this.querySelectorAll('input.dyn');
        for (var k = 0; k < inputs.length; k++) {
            if (!inputs[k].value.trim()) inputs[k].disabled = true;
        }
    });

    addQuestion();
</script>
<%@ include file="footer.jspf" %>
