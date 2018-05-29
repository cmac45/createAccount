$(function() {

  $.validator.setDefaults({
    errorClass: 'help-block',
    highlight: function(element) {
      $(element)
        .closest('.form-group')
        .addClass('has-error');
    },
    unhighlight: function(element) {
      $(element)
        .closest('.form-group')
        .removeClass('has-error');
    },
    errorPlacement: function (error, element) {
      if (element.prop('type') === 'checkbox') {
        error.insertAfter(element.parent());
      } else {
        error.insertAfter(element);
      }
    }
  });

  $.validator.addMethod('strongPassword', function(value, element) {
    return this.optional(element) 
      || value.length >= 6
      && /\d/.test(value)
      && /[a-z]/i.test(value);
  }, 'Your password must be at least 6 characters long and contain at least one number and one char\'.')

  
  $.validator.methods.email = function( value, element ) {
  return this.optional( element ) || /[A-z.]+@[a-z]+\.[a-z]+/.test( value );
}
  

  $("#register-form").validate({
    rules: {
      username: {
		required: true,
        email: true
      },
      psw1: {
        required: true,
        strongPassword: true
      },
      password2: {
        required: true,
        equalTo: '#psw1'
      }
    },
    messages: {
      email: {
        required: 'Please enter an email address.',
        email: 'Please enter a <em>valid</em> email address.',
		pattern: 'bad pattern'
      }
    }
  });

});