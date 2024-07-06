<#import "template.ftl" as layout>
<@layout.registrationLayout; section>
    <style>
        .pattern-container {
            display: flex;
            flex-direction: column;
            align-items: center;
            font-family: Arial, sans-serif;
            margin-top: 20px;
        }
        .pattern-grid {
            display: grid;
            grid-template-columns: repeat(3, 100px);
            grid-template-rows: repeat(3, 100px);
            background-color: #f0f0f0;
            gap: 15px;
            padding: 15px;
            border-radius: 10px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            margin-top: 20px;
        }
        .pattern-cell {
            width: 100px;
            height: 100px;
            background-color: #fff;
            border: 2px solid #ddd;
            border-radius: 50%;
            display: flex;
            justify-content: center;
            align-items: center;
            cursor: pointer;
            transition: background-color 0.3s, transform 0.3s;
        }
        .pattern-cell.selected {
            background-color: #007bff;
            transform: scale(1.1);
        }
        .submit-button {
            margin-top: 30px;
            padding: 10px 30px;
            background-color: #007bff;
            border: none;
            border-radius: 5px;
            color: white;
            cursor: pointer;
            font-size: 18px;
            transition: background-color 0.3s;
        }
        .submit-button:hover {
            background-color: #0056b3;
        }
        .input-error {
            color: red;
            font-size: 14px;
            margin-top: 10px;
        }
    </style>

    <script>
        document.addEventListener("DOMContentLoaded", function () {
            const cells = document.querySelectorAll(".pattern-cell");
            let pattern = [];

            function toggleSelection(cell, index) {
                if (cell.classList.contains("selected")) {
                    cell.classList.remove("selected");
                    pattern = pattern.filter((i) => i !== index);
                } else {
                    cell.classList.add("selected");
                    pattern.push(index);
                }
                // Sort the pattern array in ascending order
                pattern.sort((a, b) => a - b);
                document.getElementById("pattern").value = pattern.join(",");
            }

            cells.forEach((cell, index) => {
                cell.addEventListener("click", () => toggleSelection(cell, index));
            });
        });
    </script>
    <div class="pattern-container">
        <#if section == "title">
        ${msg("loginTitle", realm.name)}
        <#elseif section == "header">
            <h2>${msg("setUpPattern")}</h2>
        <#elseif section == "form">
        <form id="kc-pattern-form" action="${url.loginAction}" method="post">
            <div class="pattern-grid">
                <div class="pattern-cell"></div>
                <div class="pattern-cell"></div>
                <div class="pattern-cell"></div>
                <div class="pattern-cell"></div>
                <div class="pattern-cell"></div>
                <div class="pattern-cell"></div>
                <div class="pattern-cell"></div>
                <div class="pattern-cell"></div>
                <div class="pattern-cell"></div>
            </div>
            <input type="hidden" id="pattern" name="pattern" value="" />
            <div class="${properties.kcFormGroupClass!}">
                <div id="kc-form-options">
                    <div class="${properties.kcFormOptionsWrapperClass!}">
                    </div>
                </div>
                <div id="kc-form-buttons">
                    <div class="${properties.kcFormButtonsWrapperClass!}">
                        <button type="submit" class="submit-button">${msg("doSubmit")}</button>
                    </div>
                </div>
            </div>

        </form>
    </div>
</#if>
</@layout.registrationLayout>


