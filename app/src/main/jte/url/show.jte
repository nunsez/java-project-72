@import hexlet.code.dto.page.url.UrlPage
@import hexlet.code.util.NamedRoutes
@import java.text.SimpleDateFormat
@param UrlPage page

@template.layout.app(
    page = page,
    content = @`
    !{var url = page.url();}
    <div class="container-lg mt-5">
        <h1>Сайт: ${url.name()}</h1>
        <table class="table table-bordered table-hover mt-3">
            <tr>
                <td>ID</td>
                <td>${url.id()}</td>
            </tr>
            <tr>
                <td>Имя</td>
                <td>${url.name()}</td>
            </tr>
            <tr>
                <td>Дата создания</td>
                <td>${String.valueOf(url.insertedAt())}</td>
            </tr>
        </table>

        <h2 class="mt-5">Проверки</h2>
        <form method="post" action="${NamedRoutes.urlChecksPath(url)}">
            <button type="submit" class="btn btn-primary">Запустить проверку</button>
        </form>

        <table class="table table-bordered table-hover mt-3">
            <thead>
            <tr>
                <th class="col-1">ID</th>
                <th class="col-1">Код ответа</th>
                <th>title</th>
                <th>h1</th>
                <th>description</th>
                <th class="col-2">Дата проверки</th>
            </tr>
            </thead>
            <tbody>
            @for(var urlCheck : page.urlChecks())
                <tr>
                    <td>${urlCheck.id()}</td>
                    <td>${urlCheck.statusCode()}</td>
                    <td>${urlCheck.title()}</td>
                    <td>${urlCheck.h1()}</td>
                    <td>${urlCheck.description()}</td>
                    <td>${page.formatTimestamp(urlCheck.insertedAt())}</td>
                </tr>
            @endfor
            </tbody>
        </table>
    </div>
    `
)
