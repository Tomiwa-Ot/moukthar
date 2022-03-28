/*!DO NOT REMOVE!
 * MDTimePicker v2.0 plugin
 * https://github.com/dmuy/MDTimePicker
 *
 * Author: Dionlee Uy
 * Email: dionleeuy@gmail.com
 */
(function (global, factory) {
  typeof exports === 'object' && typeof module !== 'undefined' ? module.exports = factory() :
  typeof define === 'function' && define.amd ? define(factory) :
  (global = typeof globalThis !== 'undefined' ? globalThis : global || self, global.mdtimepicker = factory());
}(this, (function () { 'use strict';

  function _typeof(obj) {
    "@babel/helpers - typeof";

    if (typeof Symbol === "function" && typeof Symbol.iterator === "symbol") {
      _typeof = function (obj) {
        return typeof obj;
      };
    } else {
      _typeof = function (obj) {
        return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj;
      };
    }

    return _typeof(obj);
  }

  function _classCallCheck(instance, Constructor) {
    if (!(instance instanceof Constructor)) {
      throw new TypeError("Cannot call a class as a function");
    }
  }

  function _defineProperties(target, props) {
    for (var i = 0; i < props.length; i++) {
      var descriptor = props[i];
      descriptor.enumerable = descriptor.enumerable || false;
      descriptor.configurable = true;
      if ("value" in descriptor) descriptor.writable = true;
      Object.defineProperty(target, descriptor.key, descriptor);
    }
  }

  function _createClass(Constructor, protoProps, staticProps) {
    if (protoProps) _defineProperties(Constructor.prototype, protoProps);
    if (staticProps) _defineProperties(Constructor, staticProps);
    return Constructor;
  }

  var MDTP_DATA = '_mdtimepicker';
  /**
   * Default time picker input query selector class
   */

  var DEFAULT_CLASS = '.mdtimepicker-input';
  /**
   * Starting degree value for hour hand
   */

  var HOUR_START_DEG = 120;
  /**
   * Hour hand degree increment
   */

  var HOUR_DEG_INCR = 30;
  /**
   * Starting degree value for minute hand
   */

  var MIN_START_DEG = 90;
  /**
   * Minute hand degree increment
   */

  var MIN_DEG_INCR = 6;
  /**
   * Degree limit
   */

  var END_DEG = 360;
  /**
   * Keydown excluded key codes
   */

  var EX_KEYS = [9, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 123];
  /**
   * Default time picker configurations
   */

  var DEFAULTS = {
    // format of the time value (data-time attribute)
    timeFormat: 'hh:mm:ss.000',
    // format of the input value
    format: 'h:mm tt',
    // theme of the timepicker
    theme: 'blue',
    // determines if display value has zero padding for hour value less than 10 (i.e. 05:30 PM); 24-hour format has padding by default
    hourPadding: false,
    // determines if clear button is visible
    clearBtn: false,
    // determines if the clock will use 24-hour format in the UI; format config will be forced to `hh:mm` if not specified
    is24hour: false,
    // callback functions
    events: {
      ready: null,
      timeChanged: null,
      shown: null,
      hidden: null
    }
  };

  /**
   * Helper functions
   */
  var hf = {
    /**
     * Appends element(s) to parent
     * @param {Element|Element[]} elem Element(s) to append to parent
     * @param {Element} to Parent element
     */
    appendTo: function appendTo(elem, to, idx) {
      if (Array.isArray(elem)) {
        elem.forEach(function (el) {
          if (idx === 0) to.insertBefore(el, to.childNodes[idx] || null);else to.appendChild(el);
        });
      } else {
        if (idx === 0) to.insertBefore(elem, to.childNodes[idx] || null);else to.appendChild(elem);
      }
    },

    /**
     * Adds event listener to element(s)
     * @param {Element|Element[]} elem Element(s) to add event
     * @param {string} event Event name
     * @param {Function} handler Event handler
     */
    addEvent: function addEvent(elem, event, handler) {
      function listenEvent(el, evt, fn) {
        el.addEventListener(evt, fn, false);
      }

      if (Array.isArray(elem)) {
        elem.forEach(function (e) {
          listenEvent(e, event, handler);
        });
      } else listenEvent(elem, event, handler);
    },

    /**
     * Removes event listener to element(s)
     * @param {Element|Element[]} elem Element(s) to remove event
     * @param {string} event Event name
     * @param {Function} handler Event handler
     */
    removeEvent: function removeEvent(elem, event, handler) {
      function delEvent(el, evt, fn) {
        el.removeEventListener(evt, fn, false);
      }

      if (Array.isArray(elem)) {
        elem.forEach(function (e) {
          delEvent(e, event, handler);
        });
      } else delEvent(elem, event, handler);
    },

    /**
     * Removes child nodes
     * @param {Element} elem Html element to empty
     */
    empty: function empty(elem) {
      while (elem.firstChild) {
        elem.removeChild(elem.firstChild);
      }
    },

    /**
     * Creates an HTML element; `document.createElement` helper function
     * @see {@link http://jsfiddle.net/andr3ww/pvuzgfg6/13/}
     * @param {string} tag HTML tag name (i.e. `div`, `span`, `a`)
     * @param {Object} attributes Attribute object
     * @param {string|Element} content Element content: text or HTML element(s)
     * @param {Boolean} isHtml Determines if `content` specified should added as an html element
     */
    createElem: function createElem(tag, attributes, content, isHtml) {
      var el = document.createElement(tag);
      if (typeof content !== 'undefined') el[isHtml || false ? 'innerHTML' : 'innerText'] = content;
      if (typeof attributes !== 'undefined') hf.setAttributes(el, attributes);
      return el;
    },

    /**
     * Sets the attribute(s) of the element
     * @param {Element} el Html element
     * @param {Object} attrs Attribute object
     */
    setAttributes: function setAttributes(el, attrs) {
      for (var attr in attrs) {
        el.setAttribute(attr, attrs[attr]);
      }
    },

    /**
    * Vanilla JavaScript version of jQuery.extend()
    * @see {@link https://gomakethings.com/vanilla-javascript-version-of-jquery-extend/}
    */
    extend: function extend() {
      // Variables
      var extended = {};
      var deep = false;
      var i = 0;
      var length = arguments.length; // Check if a deep merge

      if (Object.prototype.toString.call(arguments[0]) === '[object Boolean]') {
        deep = arguments[0];
        i++;
      } // Merge the object into the extended object


      var merge = function merge(obj) {
        for (var prop in obj) {
          if (Object.prototype.hasOwnProperty.call(obj, prop)) {
            // If deep merge and property is an object, merge properties
            if (deep && Object.prototype.toString.call(obj[prop]) === '[object Object]') {
              extended[prop] = hf.extend(true, extended[prop], obj[prop]);
            } else {
              extended[prop] = obj[prop];
            }
          }
        }
      }; // Loop through each object and conduct a merge


      for (; i < length; i++) {
        var obj = arguments[i];
        merge(obj);
      }

      return extended;
    },

    /**
     * Triggers the `change`, `onchange`, `datechanged` event on the specified input element
     * @param {HTMLInputElement} el HTML input element
     * @param {Object} data Event data
     */
    triggerChange: function triggerChange(el, data) {
      el.dispatchEvent(new Event('change'));
      el.dispatchEvent(new Event('onchange'));

      function CustomEvent(data) {
        var changeEvt = document.createEvent('CustomEvent');
        changeEvt.initCustomEvent('timechanged', false, false);
        changeEvt.data = data;
        return changeEvt;
      }

      el.dispatchEvent(new CustomEvent(data));
    }
  };

  /**
   * Time class
   */

  var Time = /*#__PURE__*/function () {
    /**
     * Creates a time object
     * @param {number} hour Hour value (0 - 23)
    	 * @param {number} minute Minute value (0 - 59)
     */
    function Time(hour, minute) {
      _classCallCheck(this, Time);

      this.hour = hour;
      this.minute = minute;
    }

    _createClass(Time, [{
      key: "setHour",
      value: function setHour(value) {
        this.hour = value;
      }
    }, {
      key: "getHour",
      value: function getHour(is12Hour) {
        return is12Hour ? [0, 12].indexOf(this.hour) >= 0 ? 12 : this.hour % 12 : this.hour;
      }
    }, {
      key: "invert",
      value: function invert() {
        if (this.getPeriod() === 'AM') this.setHour(this.getHour() + 12);else this.setHour(this.getHour() - 12);
      }
    }, {
      key: "setMinutes",
      value: function setMinutes(value) {
        this.minute = value;
      }
    }, {
      key: "getMinutes",
      value: function getMinutes() {
        return this.minute;
      }
    }, {
      key: "getPeriod",
      value: function getPeriod() {
        return this.hour < 12 ? 'AM' : 'PM';
      }
    }, {
      key: "format",
      value: function format(_format, hourPadding) {
        var that = this,
            is24Hour = (_format.match(/h/g) || []).length > 1;
        return _format.replace(/(hh|h|mm|ss|tt|t)/g, function (e) {
          switch (e.toLowerCase()) {
            case 'h':
              var hour = that.getHour(true);
              return hourPadding && hour < 10 ? '0' + hour : hour;

            case 'hh':
              return that.hour < 10 ? '0' + that.hour : that.hour;

            case 'mm':
              return that.minute < 10 ? '0' + that.minute : that.minute;

            case 'ss':
              return '00';

            case 't':
              return is24Hour ? '' : that.getPeriod().toLowerCase();

            case 'tt':
              return is24Hour ? '' : that.getPeriod();
          }
        });
      }
    }]);

    return Time;
  }();
  /**
   * Time picker class
   */


  var MDTimePicker = /*#__PURE__*/function () {
    /**
     * Creates a time picker object
     * @param {HTMLInputElement} el Input element
     * @param {Object} config Time picker configurations
     */
    function MDTimePicker(el, config) {
      _classCallCheck(this, MDTimePicker);

      var _ = this;

      this.visible = false;
      this.activeView = 'hours';
      this.hTimeout = null;
      this.mTimeout = null;
      this.input = el;
      this.input.readOnly = true;
      this.config = config;
      this.time = new Time(0, 0);
      this.selected = new Time(0, 0);
      this.timepicker = {
        overlay: hf.createElem('div', {
          class: 'mdtimepicker hidden'
        }),
        wrapper: hf.createElem('div', {
          class: 'mdtp__wrapper',
          tabindex: 0
        }),
        timeHolder: {
          wrapper: hf.createElem('section', {
            class: 'mdtp__time_holder'
          }),
          hour: hf.createElem('span', {
            class: 'mdtp__time_h'
          }, '12'),
          dots: hf.createElem('span', {
            class: 'mdtp__timedots'
          }, ':'),
          minute: hf.createElem('span', {
            class: 'mdtp__time_m'
          }, '00'),
          am_pm: hf.createElem('span', {
            class: 'mdtp__ampm'
          }, 'AM')
        },
        clockHolder: {
          wrapper: hf.createElem('section', {
            class: 'mdtp__clock_holder'
          }),
          am: hf.createElem('span', {
            class: 'mdtp__am'
          }, 'AM'),
          pm: hf.createElem('span', {
            class: 'mdtp__pm'
          }, 'PM'),
          clock: {
            wrapper: hf.createElem('div', {
              class: 'mdtp__clock'
            }),
            dot: hf.createElem('span', {
              class: 'mdtp__clock_dot'
            }),
            hours: hf.createElem('div', {
              class: 'mdtp__hour_holder'
            }),
            minutes: hf.createElem('div', {
              class: 'mdtp__minute_holder'
            })
          },
          buttonsHolder: {
            wrapper: hf.createElem('div', {
              class: 'mdtp__buttons'
            }),
            btnClear: hf.createElem('span', {
              class: 'mdtp__button clear-btn'
            }, 'Clear'),
            btnOk: hf.createElem('span', {
              class: 'mdtp__button ok'
            }, 'Ok'),
            btnCancel: hf.createElem('span', {
              class: 'mdtp__button cancel'
            }, 'Cancel')
          }
        }
      };
      this.setMinTime(this.input.dataset.mintime || this.config.minTime);
      this.setMaxTime(this.input.dataset.maxtime || this.config.maxTime);
      var picker = _.timepicker;
      hf.appendTo(_._setup(), document.body);
      hf.addEvent(picker.overlay, 'click', function () {
        _.hide();
      });
      hf.addEvent(picker.wrapper, 'click', function (e) {
        return e.stopPropagation();
      });
      hf.addEvent(picker.wrapper, 'keydown', function (e) {
        if (e.keyCode !== 27) return;

        _.hide();
      });
      hf.addEvent(picker.clockHolder.am, 'click', function () {
        if (_.selected.getPeriod() !== 'AM') _.setPeriod('am');
      });
      hf.addEvent(picker.clockHolder.pm, 'click', function () {
        if (_.selected.getPeriod() !== 'PM') _.setPeriod('pm');
      });
      hf.addEvent(picker.timeHolder.hour, 'click', function () {
        if (_.activeView !== 'hours') _._switchView('hours');
      });
      hf.addEvent(picker.timeHolder.minute, 'click', function () {
        if (_.activeView !== 'minutes') _._switchView('minutes');
      });
      hf.addEvent(picker.clockHolder.buttonsHolder.btnOk, 'click', function () {
        var selected = _.selected;
        if (_.isDisabled(selected.getHour(), selected.getMinutes(), false)) return;

        _.setValue(selected);

        var formatted = _.getFormattedTime();

        _._triggerChange({
          time: formatted.time,
          value: formatted.value
        });

        _.hide();
      });
      hf.addEvent(picker.clockHolder.buttonsHolder.btnCancel, 'click', function () {
        _.hide();
      });

      if (_.config.clearBtn) {
        hf.addEvent(picker.clockHolder.buttonsHolder.btnClear, 'click', function () {
          _.input.value = '';
          hf.setAttributes(_.input, {
            'value': '',
            'data-time': null
          });

          _._triggerChange({
            time: null,
            value: ''
          });

          _.hide();
        });
      }
      /* input event handlers */


      function _inputClick() {
        _.show();
      }

      function _inputKeydown(e) {
        if (e.keyCode === 13) {
          _.show();
        }

        return !(EX_KEYS.indexOf(e.which) < 0);
      }
      /**
       * Unbinds input `click` and `keydown` event handlers
       */


      this._unbindInput = function () {
        _.input.readOnly = false;

        _.input.removeEventListener('click', _inputClick);

        _.input.removeEventListener('keydown', _inputKeydown);
      };

      hf.addEvent(_.input, 'keydown', _inputKeydown);
      hf.addEvent(_.input, 'click', _inputClick);

      if (_.input.value !== '') {
        var time = _.parseTime(_.input.value, _.config.format);

        _.setValue(time);
      } else {
        var _time = _.getSystemTime();

        _.time = new Time(_time.hour, _time.minute);
      }

      _.resetSelected();

      _._switchView(_.activeView);

      if (_.config.events && _.config.events.ready) _.config.events.ready.call(_, _);
    }
    /**
     * Setup time picker html elements
     */


    _createClass(MDTimePicker, [{
      key: "_setup",
      value: function _setup() {
        var _ = this,
            picker = _.timepicker,
            overlay = picker.overlay,
            wrapper = picker.wrapper,
            time = picker.timeHolder,
            clock = picker.clockHolder;

        hf.appendTo([time.hour, time.dots, time.minute], time.wrapper);
        hf.appendTo(time.wrapper, wrapper);
        if (!_.config.is24hour) hf.appendTo(time.am_pm, time.wrapper); // Setup hours

        var _hours = _.config.is24hour ? 24 : 12;

        for (var i = 0; i < _hours; i++) {
          var value = i + 1,
              deg = (HOUR_START_DEG + i * HOUR_DEG_INCR) % END_DEG - (_.config.is24hour && value < 13 ? 15 : 0),
              is24 = value === 24,
              hour = hf.createElem('div', {
            class: "mdtp__digit rotate-".concat(deg),
            'data-hour': is24 ? 0 : value
          }),
              hourInner = hf.createElem('span', null, is24 ? '00' : value);
          hf.appendTo(hourInner, hour);
          if (_.config.is24hour && value < 13) hour.classList.add('inner--digit');
          hf.addEvent(hourInner, 'click', function () {
            var _hour = parseInt(this.parentNode.dataset.hour),
                _selectedT = _.selected.getPeriod(),
                _value = _.config.is24hour ? _hour : (_hour + (_selectedT === 'PM' && _hour < 12 || _selectedT === 'AM' && _hour === 12 ? 12 : 0)) % 24,
                disabled = _.isDisabled(_value, 0, true);

            if (disabled) return;

            _.setHour(_value);

            _._switchView('minutes');
          });
          hf.appendTo(hour, clock.clock.hours);
        } // Setup minutes


        for (var _i = 0; _i < 60; _i++) {
          var min = _i < 10 ? '0' + _i : _i,
              _deg = (MIN_START_DEG + _i * MIN_DEG_INCR) % END_DEG,
              minute = hf.createElem('div', {
            class: "mdtp__digit rotate-".concat(_deg),
            'data-minute': _i
          }),
              minuteInner = hf.createElem('span');

          hf.appendTo(minuteInner, minute);

          if (_i % 5 === 0) {
            minute.classList.add('marker');
            minuteInner.innerText = min;
          }

          hf.addEvent(minuteInner, 'click', function () {
            var _minute = parseInt(this.parentNode.dataset.minute),
                _hour = _.selected.getHour(),
                disabled = _.isDisabled(_hour, _minute, true);

            if (disabled) return;

            _.setMinute(_minute);
          });
          hf.appendTo(minute, clock.clock.minutes);
        } // Setup clock


        if (!_.config.is24hour) {
          hf.appendTo([clock.am, clock.pm], clock.clock.wrapper);
        }

        hf.appendTo([clock.clock.dot, clock.clock.hours, clock.clock.minutes], clock.clock.wrapper);
        hf.appendTo(clock.clock.wrapper, clock.wrapper); // Setup buttons

        if (_.config.clearBtn) {
          hf.appendTo(clock.buttonsHolder.btnClear, clock.buttonsHolder.wrapper);
        }

        hf.appendTo([clock.buttonsHolder.btnCancel, clock.buttonsHolder.btnOk], clock.buttonsHolder.wrapper);
        hf.appendTo(clock.buttonsHolder.wrapper, clock.wrapper);
        hf.appendTo(clock.wrapper, wrapper); // Setup theme

        wrapper.dataset.theme = _.input.dataset.theme || _.config.theme;
        hf.appendTo(wrapper, overlay);
        return overlay;
      }
      /**
       * Sets the hour value of the selected time
       * @param {number} hour Hour value
       */

    }, {
      key: "setHour",
      value: function setHour(hour) {
        if (typeof hour === 'undefined') throw new Error('Expecting a value.');
        var is12Hour = !this.config.is24hour;
        this.selected.setHour(hour);

        var _selectedH = this.selected.getHour(is12Hour);

        this.timepicker.timeHolder.hour.innerText = is12Hour ? _selectedH : this.selected.format('hh');
        this.timepicker.clockHolder.clock.hours.querySelectorAll('div').forEach(function (div) {
          var val = parseInt(div.dataset.hour);
          div.classList[val === _selectedH ? 'add' : 'remove']('active');
        });
      }
      /**
       * Sets the minute value of the selected time
       * @param {number} minute Minute value
       */

    }, {
      key: "setMinute",
      value: function setMinute(minute) {
        if (typeof minute === 'undefined') throw new Error('Expecting a value.');
        this.selected.setMinutes(minute);
        this.timepicker.timeHolder.minute.innerText = minute < 10 ? '0' + minute : minute;
        this.timepicker.clockHolder.clock.minutes.querySelectorAll('div').forEach(function (div) {
          var val = parseInt(div.dataset.minute);
          div.classList[val === minute ? 'add' : 'remove']('active');
        });
      }
      /**
       * Sets the time period of the selected time
       * @param {string} period Period value (AM/PM)
       */

    }, {
      key: "setPeriod",
      value: function setPeriod(period) {
        if (typeof period === 'undefined') throw new Error('Expecting a value.');
        if (this.selected.getPeriod() !== period.toUpperCase()) this.selected.invert();

        var _period = this.selected.getPeriod();

        this._setDisabled(this.activeView);

        this.timepicker.timeHolder.am_pm.innerText = _period;
        this.timepicker.clockHolder.am.classList[_period === 'AM' ? 'add' : 'remove']('active');
        this.timepicker.clockHolder.pm.classList[_period === 'PM' ? 'add' : 'remove']('active');
      }
      /**
       * Sets the value of the selected time
       * @param {string} value Time string values
       */

    }, {
      key: "setValue",
      value: function setValue(value) {
        if (typeof value === 'undefined') throw new Error('Expecting a value.');
        var time = typeof value === 'string' ? this.parseTime(value, this.config.format) : value;
        this.time = new Time(time.hour, time.minute);
        var formatted = this.getFormattedTime();
        this.input.value = formatted.value;
        hf.setAttributes(this.input, {
          'value': formatted.value,
          'data-time': formatted.time
        });
      }
      /**
       * Sets the minimum time constraint
       * @param {string} time Minimum time value
       */

    }, {
      key: "setMinTime",
      value: function setMinTime(time) {
        this.minTime = time;
      }
      /**
       * Sets the maximum time constraint
       * @param {string} time Maximum time value
       */

    }, {
      key: "setMaxTime",
      value: function setMaxTime(time) {
        this.maxTime = time;
      }
      /**
       * Sets the disabled digits of the clock
       * @param {string} view View name
       */

    }, {
      key: "_setDisabled",
      value: function _setDisabled(view) {
        if (view !== 'hours' && view !== 'minutes') return;

        var _ = this,
            clock = this.timepicker.clockHolder.clock;

        if (view === 'hours') {
          clock.hours.querySelectorAll('.mdtp__digit').forEach(function (hour) {
            var value = parseInt(hour.dataset.hour),
                period = _.selected.getPeriod(),
                time = new Time(value, 0);

            if (!_.config.is24hour && period !== time.getPeriod()) time.invert();

            var disabled = _.isDisabled(time.getHour(), 0, true);

            hour.classList[disabled ? 'add' : 'remove']('digit--disabled');
          });
        }

        if (view === 'minutes') {
          clock.minutes.querySelectorAll('.mdtp__digit').forEach(function (minute) {
            var value = parseInt(minute.dataset.minute),
                hour = _.selected.getHour(),
                disabled = _.isDisabled(hour, value, true);

            minute.classList[disabled ? 'add' : 'remove']('digit--disabled');
          });
        }
      }
      /**
       * Determines if the given time is disabled
       * @param {number} hour Hour value
       * @param {number} minute Minute value
       * @param {boolean} renderMode `true` if called upon rendering; `false` otherwise
       */

    }, {
      key: "isDisabled",
      value: function isDisabled(hour, minute, renderMode) {
        var _ = this,
            minT = null,
            min = null,
            maxT = null,
            max = null,
            now = new Date(),
            time = new Date(now.getFullYear(), now.getMonth(), now.getDate(), hour, minute, 0, 0),
            hourView = _.activeView === 'hours';

        if (_.minTime) minT = _.minTime === 'now' ? _.getSystemTime() : _.parseTime(_.minTime);
        if (_.maxTime) maxT = _.maxTime === 'now' ? _.getSystemTime() : _.parseTime(_.maxTime);

        if (minT) {
          min = new Date(now.getFullYear(), now.getMonth(), now.getDate(), minT.getHour(), hourView && renderMode ? 0 : minT.getMinutes(), 0, 0);
        }

        if (maxT) {
          max = new Date(now.getFullYear(), now.getMonth(), now.getDate(), maxT.getHour(), hourView && renderMode ? 0 : maxT.getMinutes(), 0, 0);
        }

        return min && time < min || max && time > max;
      }
      /**
       * Resets the selected time to client (system) time
       */

    }, {
      key: "resetSelected",
      value: function resetSelected() {
        this.setHour(this.time.hour);
        this.setMinute(this.time.minute);
        this.setPeriod(this.time.getPeriod());
      }
      /**
       * Returns the selected time string
       */

    }, {
      key: "getFormattedTime",
      value: function getFormattedTime() {
        var time = this.time.format(this.config.timeFormat, false),
            tValue = this.time.format(this.config.format, this.config.hourPadding);
        return {
          time: time,
          value: tValue
        };
      }
      /**
       * Returns the current client (system) time
       */

    }, {
      key: "getSystemTime",
      value: function getSystemTime() {
        return function (now) {
          return new Time(now.getHours(), now.getMinutes());
        }(new Date());
      }
      /**
       * Parses the given time string into a Time object
       * @param {string} time Time value
       * @param {string} tf Time format
       */

    }, {
      key: "parseTime",
      value: function parseTime(time, tf) {
        var that = this,
            format = typeof tf === 'undefined' ? that.config.format : tf,
            hLength = (format.match(/h/g) || []).length,
            is24Hour = hLength > 1,
            // mLength = (format.match(/m/g) || []).length, 
        tLength = (format.match(/t/g) || []).length,
            timeLength = time.length,
            fH = format.indexOf('h'),
            lH = format.lastIndexOf('h'),
            hour = '',
            min = '',
            t = ''; // Parse hour

        if (that.config.hourPadding || is24Hour) {
          hour = time.substr(fH, 2);
        } else {
          var prev = format.substring(fH - 1, fH),
              next = format.substring(lH + 1, lH + 2);

          if (lH === format.length - 1) {
            hour = time.substring(time.indexOf(prev, fH - 1) + 1, timeLength);
          } else if (fH === 0) {
            hour = time.substring(0, time.indexOf(next, fH));
          } else {
            hour = time.substring(time.indexOf(prev, fH - 1) + 1, time.indexOf(next, fH + 1));
          }
        }

        format = format.replace(/(hh|h)/g, hour);
        var fM = format.indexOf('m'),
            lM = format.lastIndexOf('m'),
            fT = format.indexOf('t'); // Parse minute

        var prevM = format.substring(fM - 1, fM),
            nextM = format.substring(lM + 1, lM + 2);

        if (lM === format.length - 1) {
          min = time.substring(time.indexOf(prevM, fM - 1) + 1, timeLength);
        } else if (fM === 0) {
          min = time.substring(0, 2);
        } else {
          min = time.substr(fM, 2);
        } // Parse t (am/pm)


        if (is24Hour) t = parseInt(hour) > 11 ? tLength > 1 ? 'PM' : 'pm' : tLength > 1 ? 'AM' : 'am';else t = time.substr(fT, 2);
        var isPm = t.toLowerCase() === 'pm',
            outTime = new Time(parseInt(hour), parseInt(min));

        if (isPm && parseInt(hour) < 12 || !isPm && parseInt(hour) === 12) {
          outTime.invert();
        }

        return outTime;
      }
      /**
       * Switches the time picker view (screen)
       * @param {string} view View name
       */

    }, {
      key: "_switchView",
      value: function _switchView(view) {
        var _ = this,
            picker = this.timepicker,
            anim_speed = 350;

        if (view !== 'hours' && view !== 'minutes') return;
        _.activeView = view;

        _._setDisabled(view);

        picker.timeHolder.hour.classList[view === 'hours' ? 'add' : 'remove']('active');
        picker.timeHolder.minute.classList[view === 'hours' ? 'remove' : 'add']('active');
        picker.clockHolder.clock.hours.classList.add('animate');
        if (view === 'hours') picker.clockHolder.clock.hours.classList.remove('hidden');
        clearTimeout(_.hTimeout);
        _.hTimeout = setTimeout(function () {
          if (view !== 'hours') picker.clockHolder.clock.hours.classList.add('hidden');
          picker.clockHolder.clock.hours.classList.remove('animate');
        }, view === 'hours' ? 20 : anim_speed);
        picker.clockHolder.clock.minutes.classList.add('animate');
        if (view === 'minutes') picker.clockHolder.clock.minutes.classList.remove('hidden');
        clearTimeout(_.mTimeout);
        _.mTimeout = setTimeout(function () {
          if (view !== 'minutes') picker.clockHolder.clock.minutes.classList.add('hidden');
          picker.clockHolder.clock.minutes.classList.remove('animate');
        }, view === 'minutes' ? 20 : anim_speed);
      }
      /**
       * Shows the time picker
       */

    }, {
      key: "show",
      value: function show() {
        var _ = this;

        if (_.input.value === '') {
          var time = _.getSystemTime();

          this.time = new Time(time.hour, time.minute);
        }

        _.resetSelected();

        document.body.setAttribute('mdtimepicker-display', 'on');

        _.timepicker.wrapper.classList.add('animate');

        _.timepicker.overlay.classList.remove('hidden');

        _.timepicker.overlay.classList.add('animate');

        setTimeout(function () {
          _.timepicker.overlay.classList.remove('animate');

          _.timepicker.wrapper.classList.remove('animate');

          _.timepicker.wrapper.focus();

          _.visible = true;

          _.input.blur();

          if (_.config.events && _.config.events.shown) _.config.events.shown.call(_);
        }, 10);
      }
      /**
       * Hides the time picker
       */

    }, {
      key: "hide",
      value: function hide() {
        var _ = this;

        _.timepicker.overlay.classList.add('animate');

        _.timepicker.wrapper.classList.add('animate');

        setTimeout(function () {
          _._switchView('hours');

          _.timepicker.overlay.classList.add('hidden');

          _.timepicker.overlay.classList.remove('animate');

          _.timepicker.wrapper.classList.remove('animate');

          document.body.removeAttribute('mdtimepicker-display');
          _.visible = false;

          _.input.focus();

          if (_.config.events && _.config.events.hidden) _.config.events.hidden.call(_);
        }, 300);
      }
      /**
       * Removes the time picker
       */

    }, {
      key: "destroy",
      value: function destroy() {
        this._unbindInput();

        this.timepicker.overlay.remove();
        delete this.input[MDTP_DATA];
      }
      /**
       * Triggers the change event on the input element
       * @param {Object} data Event data
       */

    }, {
      key: "_triggerChange",
      value: function _triggerChange(data) {
        hf.triggerChange(this.input, data);
        if (this.config.events && this.config.events.timeChanged) this.config.events.timeChanged.call(this, data, this);
      }
    }]);

    return MDTimePicker;
  }();
  /**
   * Time picker wrapper
   */


  function mdtimepicker() {
    var args = arguments,
        arg0 = args[0],
        arg0IsList = arg0 instanceof NodeList || Array.isArray(arg0),
        arg0IsElem = arg0 instanceof Element,
        inputs = typeof arg0 === 'string' ? document.querySelectorAll(arg0) : arg0IsList ? arg0 : arg0IsElem ? [arg0] : document.querySelectorAll(DEFAULT_CLASS),
        options = _typeof(arg0) === 'object' && !arg0IsList && !arg0IsElem ? arg0 : args[1] && _typeof(args[1]) === 'object' ? args[1] : {},
        _defaults = hf.extend({}, DEFAULTS);

    if (options && options.is24hour) _defaults.format = 'hh:mm';
    Array.from(inputs).forEach(function (el) {
      var picker = el[MDTP_DATA];

      if (!picker) {
        el[MDTP_DATA] = picker = new MDTimePicker(el, hf.extend(_defaults, options));
      }

      if ((typeof arg0 === 'string' || arg0IsList || arg0IsElem) && args[1] && typeof args[1] === 'string') {
        picker[args[1]].apply(picker, Array.prototype.slice.call(args).slice(2));
      }
    });
  }

  return mdtimepicker;

})));
