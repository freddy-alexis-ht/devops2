// Esto indica que esperará a que el DOM esté completamente cargado..
// ..y luego delegará el procesamiento al método 'main'
$( document ).ready( main );

function main() {

    $('.btn-collapse').click(function(e) {
        e.preventDefault();
        var $this = $(this);
        var $collapse = $this.closest('.collapse-group').find('.collapse');
        $collapse.collapse('toggle'); // al hacer clic mostrará y ocultará
    });

}